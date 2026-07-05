package cn.iocoder.yudao.module.aistudy.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow.AiKnowledgePointRespVO;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow.AiLearningFlowSummaryRespVO;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow.AiQuizSubmitReqVO;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow.AiQuizSubmitRespVO;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow.AiReportRespVO;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow.AiReviewSubmitRespVO;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow.AiUserLearningProfileRespVO;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiKnowledgePointDO;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiLearningEventDO;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiSkillDO;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiSkillProgressDO;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiTutorMemoryDO;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiUserLearningProfileDO;
import cn.iocoder.yudao.module.aistudy.dal.mysql.AiKnowledgePointMapper;
import cn.iocoder.yudao.module.aistudy.dal.mysql.AiLearningEventMapper;
import cn.iocoder.yudao.module.aistudy.dal.mysql.AiSkillMapper;
import cn.iocoder.yudao.module.aistudy.dal.mysql.AiSkillProgressMapper;
import cn.iocoder.yudao.module.aistudy.dal.mysql.AiTutorMemoryMapper;
import cn.iocoder.yudao.module.aistudy.dal.mysql.AiUserLearningProfileMapper;
import cn.iocoder.yudao.module.aistudy.enums.AiLearningEventTypeEnum;
import cn.iocoder.yudao.module.aistudy.enums.AiReviewStageEnum;
import cn.iocoder.yudao.module.aistudy.framework.dify.DifyChatMessageReqDTO;
import cn.iocoder.yudao.module.aistudy.framework.dify.DifyChatMessageRespDTO;
import cn.iocoder.yudao.module.aistudy.framework.dify.DifyChatflowClient;
import cn.iocoder.yudao.module.aistudy.framework.dify.DifyGrowthReportRespDTO;
import cn.iocoder.yudao.module.aistudy.framework.dify.DifyLearningResponseDTO;
import cn.iocoder.yudao.module.aistudy.framework.dify.DifyQuizJudgeRespDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AiLearningFlowServiceImpl implements AiLearningFlowService {

    private static final String DEFAULT_TUTOR = "default";
    private static final String TEXT_SEPARATOR = "；";
    private static final int MAX_MEMORY_LENGTH = 2000;

    @Resource
    private AiKnowledgePointMapper knowledgePointMapper;
    @Resource
    private AiLearningEventMapper learningEventMapper;
    @Resource
    private AiUserLearningProfileMapper profileMapper;
    @Resource
    private AiTutorMemoryMapper tutorMemoryMapper;
    @Resource
    private AiSkillProgressMapper skillProgressMapper;
    @Resource
    private AiSkillMapper skillMapper;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private DifyChatflowClient difyChatflowClient;

    @Override
    public DifyLearningResponseDTO parseLearningResponse(String rawResponse) {
        if (StrUtil.isBlank(rawResponse)) {
            return fallback(rawResponse);
        }
        try {
            return objectMapper.readValue(rawResponse, DifyLearningResponseDTO.class);
        } catch (Exception ignored) {
            return fallback(rawResponse);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiLearningFlowRecordResult recordChatLearning(Long userId, Long skillId, Long chatId, DifyLearningResponseDTO learningResponse, String tutorCode) {
        Integer beforeXp = defaultInt(ensureProfile(userId).getTotalXp());
        writeEvent(userId, skillId, AiLearningEventTypeEnum.CHAT, chatId, "{}");
        earnXp(userId, AiLearningEventTypeEnum.CHAT.getXp(), AiLearningEventTypeEnum.CHAT.getType(), skillId, chatId);
        Integer mastery = null;
        if (skillId == null) {
            return buildRecordResult(userId, beforeXp, mastery);
        }

        DifyLearningResponseDTO learning = learningResponse == null ? new DifyLearningResponseDTO() : learningResponse;
        String tutor = StrUtil.blankToDefault(tutorCode, DEFAULT_TUTOR);
        List<AiKnowledgePointDO> points = addKnowledgePoints(userId, skillId, chatId, learning.getKnowledgePoints(), tutor);
        updateTutorMemoryByLearning(userId, skillId, tutor, learning, points);
        mastery = recomputeSkillMastery(userId, skillId);
        return buildRecordResult(userId, beforeXp, mastery);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<AiKnowledgePointDO> addKnowledgePoints(Long userId, Long skillId, Long chatId, List<String> points, String tutorCode) {
        if (CollUtil.isEmpty(points) || skillId == null) {
            return Collections.emptyList();
        }
        String batchId = UUID.randomUUID().toString();
        List<AiKnowledgePointDO> result = new ArrayList<>();
        for (String point : points) {
            String content = StrUtil.trim(point);
            String normalized = normalizePoint(content);
            if (StrUtil.isBlank(normalized) || existsKnowledgePoint(userId, skillId, normalized)) {
                continue;
            }
            AiKnowledgePointDO item = AiKnowledgePointDO.builder()
                    .userId(userId)
                    .skillId(skillId)
                    .content(content)
                    .sourceChatId(chatId)
                    .stage(0)
                    .nextReviewTime(LocalDateTime.now())
                    .taughtBy(StrUtil.blankToDefault(tutorCode, DEFAULT_TUTOR))
                    .batchId(batchId)
                    .build();
            knowledgePointMapper.insert(item);
            writeEvent(userId, skillId, AiLearningEventTypeEnum.KP_ADD, item.getId(), "{}");
            earnXp(userId, AiLearningEventTypeEnum.KP_ADD.getXp(), AiLearningEventTypeEnum.KP_ADD.getType(), skillId, item.getId());
            result.add(item);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiReviewSubmitRespVO reviewKnowledgePoint(Long userId, Long knowledgePointId, Boolean remembered) {
        AiKnowledgePointDO point = knowledgePointMapper.selectById(knowledgePointId);
        if (point == null || !Objects.equals(point.getUserId(), userId)) {
            throw new IllegalArgumentException("knowledge point not found");
        }
        Integer newStage = AiReviewStageEnum.nextStage(point.getStage(), remembered);
        point.setStage(newStage);
        point.setLastReviewTime(LocalDateTime.now());
        Integer intervalHours = AiReviewStageEnum.intervalHours(newStage);
        point.setNextReviewTime(intervalHours == null ? null : LocalDateTime.now().plusHours(intervalHours));
        knowledgePointMapper.updateById(point);
        writeEvent(userId, point.getSkillId(), AiLearningEventTypeEnum.REVIEW, point.getId(), "{\"remembered\":" + Boolean.TRUE.equals(remembered) + "}");
        earnXp(userId, AiLearningEventTypeEnum.REVIEW.getXp(), AiLearningEventTypeEnum.REVIEW.getType(), point.getSkillId(), point.getId());
        Integer mastery = recomputeSkillMastery(userId, point.getSkillId());

        AiReviewSubmitRespVO respVO = new AiReviewSubmitRespVO();
        respVO.setNewStage(newStage);
        respVO.setNextReviewTime(point.getNextReviewTime());
        respVO.setMastery(mastery);
        respVO.setXpGained(AiLearningEventTypeEnum.REVIEW.getXp());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiQuizSubmitRespVO submitQuizAnswer(Long userId, AiQuizSubmitReqVO reqVO) {
        DifyQuizJudgeRespDTO judge = judgeQuizByDify(userId, reqVO);
        boolean correct = Boolean.TRUE.equals(judge.getCorrect());
        AiLearningEventTypeEnum eventType = correct ? AiLearningEventTypeEnum.QUIZ_CORRECT : AiLearningEventTypeEnum.QUIZ_WRONG;

        writeEvent(userId, reqVO.getSkillId(), eventType, reqVO.getChatId(), buildQuizMeta(reqVO, judge, correct));
        int beforeXp = defaultInt(ensureProfile(userId).getTotalXp());
        AiUserLearningProfileDO afterProfile = earnXp(userId, eventType.getXp(), eventType.getType(), reqVO.getSkillId(), reqVO.getChatId());
        Integer mastery = recomputeSkillMastery(userId, reqVO.getSkillId());

        AiQuizSubmitRespVO respVO = new AiQuizSubmitRespVO();
        respVO.setCorrect(correct);
        respVO.setScore(judge.getScore());
        respVO.setFeedback(StrUtil.blankToDefault(judge.getFeedback(), correct ? "回答正确。" : "回答还不够准确，建议回看参考答案。"));
        respVO.setXpGained(Math.max(0, defaultInt(afterProfile.getTotalXp()) - beforeXp));
        respVO.setMastery(mastery);
        respVO.setTotalXp(defaultInt(afterProfile.getTotalXp()));
        respVO.setLevelNo(defaultInt(afterProfile.getLevelNo()));
        return respVO;
    }

    @Override
    public Integer recomputeSkillMastery(Long userId, Long skillId) {
        Integer mastery = computeMastery(userId, skillId);
        AiSkillProgressDO progress = skillProgressMapper.selectByUserIdAndSkillId(userId, skillId);
        if (progress == null) {
            progress = AiSkillProgressDO.builder()
                    .userId(userId)
                    .skillId(skillId)
                    .progress(mastery)
                    .status(mastery >= 80 ? 1 : 0)
                    .lastStudyTime(LocalDateTime.now())
                    .build();
            skillProgressMapper.insert(progress);
        } else {
            progress.setProgress(mastery);
            progress.setStatus(mastery >= 80 ? 1 : 0);
            progress.setLastStudyTime(LocalDateTime.now());
            skillProgressMapper.updateById(progress);
        }
        return mastery;
    }

    @Override
    public List<AiKnowledgePointRespVO> getTodayReviews(Long userId) {
        List<AiKnowledgePointRespVO> list = BeanUtils.toBean(knowledgePointMapper.selectDueReviews(userId, LocalDateTime.now()), AiKnowledgePointRespVO.class);
        list.forEach(item -> {
            AiSkillDO skill = skillMapper.selectById(item.getSkillId());
            item.setSkillName(skill == null ? "" : skill.getName());
        });
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiUserLearningProfileDO earnXp(Long userId, Integer amount, String source, Long skillId, Long refId) {
        AiUserLearningProfileDO profile = ensureProfile(userId);
        LocalDate today = LocalDate.now();
        int bonus = 0;
        if (!today.equals(profile.getLastActiveDate())) {
            int streak = today.minusDays(1).equals(profile.getLastActiveDate()) ? defaultInt(profile.getCurrentStreak()) + 1 : 1;
            profile.setCurrentStreak(streak);
            profile.setLongestStreak(Math.max(defaultInt(profile.getLongestStreak()), streak));
            profile.setLastActiveDate(today);
            bonus = Math.min(streak * 10, 50);
        }
        int xp = defaultInt(amount) + bonus;
        profile.setTotalXp(defaultInt(profile.getTotalXp()) + xp);
        profile.setLevelNo(computeLevel(profile.getTotalXp()));
        profile.setWeeklyXp(updateWeeklyXp(profile.getWeeklyXp(), today, xp));
        profileMapper.updateById(profile);
        return profile;
    }

    @Override
    public String buildTutorMemoryPrompt(Long userId, Long skillId, String tutorCode) {
        AiTutorMemoryDO memory = tutorMemoryMapper.selectByUserSkillTutor(userId, skillId, StrUtil.blankToDefault(tutorCode, DEFAULT_TUTOR));
        if (memory == null) {
            return "";
        }
        return "已讲授：" + StrUtil.blankToDefault(memory.getTaughtSummary(), "")
                + "\n当前卡点：" + StrUtil.blankToDefault(memory.getStuckPoints(), "")
                + "\n下次建议：" + StrUtil.blankToDefault(memory.getNextSuggestion(), "");
    }

    @Override
    public void updateTutorMemory(Long userId, Long skillId, String tutorCode, String taughtSummary, String stuckPoints, String nextSuggestion) {
        if (skillId == null) {
            return;
        }
        String tutor = StrUtil.blankToDefault(tutorCode, DEFAULT_TUTOR);
        AiTutorMemoryDO memory = tutorMemoryMapper.selectByUserSkillTutor(userId, skillId, tutor);
        if (memory == null) {
            memory = AiTutorMemoryDO.builder()
                    .userId(userId)
                    .skillId(skillId)
                    .tutorCode(tutor)
                    .taughtSummary(limitText(taughtSummary))
                    .stuckPoints(limitText(stuckPoints))
                    .nextSuggestion(limitText(nextSuggestion))
                    .attitude("learning")
                    .build();
            tutorMemoryMapper.insert(memory);
            return;
        }
        memory.setTaughtSummary(limitText(merge(memory.getTaughtSummary(), taughtSummary)));
        memory.setStuckPoints(limitText(merge(memory.getStuckPoints(), stuckPoints)));
        memory.setNextSuggestion(limitText(StrUtil.blankToDefault(nextSuggestion, memory.getNextSuggestion())));
        memory.setAttitude("improving");
        tutorMemoryMapper.updateById(memory);
    }

    @Override
    public AiUserLearningProfileRespVO getProfile(Long userId) {
        AiUserLearningProfileDO profile = ensureProfile(userId);
        return BeanUtils.toBean(profile, AiUserLearningProfileRespVO.class);
    }

    @Override
    public AiLearningFlowSummaryRespVO getSummary(Long userId) {
        AiLearningFlowSummaryRespVO respVO = new AiLearningFlowSummaryRespVO();
        respVO.setProfile(getProfile(userId));
        respVO.setTodayReviewCount(knowledgePointMapper.selectDueReviews(userId, LocalDateTime.now()).size());
        List<AiSkillProgressDO> progresses = skillProgressMapper.selectListByUserId(userId);
        respVO.setMasteryRanking(progresses.stream().map(item -> {
            Map<String, Object> map = new LinkedHashMap<>();
            AiSkillDO skill = skillMapper.selectById(item.getSkillId());
            map.put("skillId", item.getSkillId());
            map.put("skillName", skill == null ? "未命名技能" : skill.getName());
            map.put("mastery", item.getProgress());
            return map;
        }).collect(Collectors.toList()));
        Map<Integer, Long> stageMap = knowledgePointMapper.selectList(new LambdaQueryWrapper<AiKnowledgePointDO>()
                        .eq(AiKnowledgePointDO::getUserId, userId))
                .stream().collect(Collectors.groupingBy(item -> defaultInt(item.getStage()), Collectors.counting()));
        respVO.setStageDistribution(stageMap.entrySet().stream().map(entry -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("stage", entry.getKey());
            map.put("count", entry.getValue());
            return map;
        }).collect(Collectors.toList()));
        respVO.setRecentEvents(learningEventMapper.selectRecentList(userId, 20).stream().map(item -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("type", item.getEventType());
            map.put("xp", item.getScoreDelta());
            map.put("time", item.getCreateTime());
            return map;
        }).collect(Collectors.toList()));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiReportRespVO generateReport(Long userId) {
        AiReportRespVO respVO = buildGrowthReportByDify(userId);
        writeEvent(userId, null, AiLearningEventTypeEnum.REPORT_GENERATE, null, "{}");
        earnXp(userId, AiLearningEventTypeEnum.REPORT_GENERATE.getXp(), AiLearningEventTypeEnum.REPORT_GENERATE.getType(), null, null);
        return respVO;
    }

    private void updateTutorMemoryByLearning(Long userId, Long skillId, String tutor, DifyLearningResponseDTO learning, List<AiKnowledgePointDO> points) {
        DifyLearningResponseDTO.DifyTutorMemoryUpdateDTO update = learning.getTutorMemoryUpdate();
        if (update != null) {
            updateTutorMemory(userId, skillId, tutor,
                    update.getTaughtSummary(),
                    update.getStuckPoints(),
                    update.getNextSuggestion());
            return;
        }
        if (CollUtil.isNotEmpty(points) || CollUtil.isNotEmpty(learning.getStuckPoints()) || StrUtil.isNotBlank(learning.getNextSuggestion())) {
            updateTutorMemory(userId, skillId, tutor,
                    points.stream().map(AiKnowledgePointDO::getContent).collect(Collectors.joining(TEXT_SEPARATOR)),
                    String.join(TEXT_SEPARATOR, CollUtil.emptyIfNull(learning.getStuckPoints())),
                    learning.getNextSuggestion());
        }
    }

    private Integer computeMastery(Long userId, Long skillId) {
        List<AiKnowledgePointDO> points = knowledgePointMapper.selectListByUserSkill(userId, skillId);
        int count = points.size();
        if (count == 0) {
            return 0;
        }
        int totalStage = points.stream().mapToInt(item -> defaultInt(item.getStage())).sum();
        double kpMastery = totalStage * 1.0 / (count * 5) * 90;
        long chatCount = learningEventMapper.selectCountByUserSkillAndType(userId, skillId, AiLearningEventTypeEnum.CHAT.getType());
        double roundBonus = Math.min(chatCount / 30.0, 1.0) * 10;
        int mastery = (int) Math.round(kpMastery + roundBonus);
        if (count < 3) {
            mastery = Math.min(mastery, 15);
        }
        return Math.max(0, Math.min(100, mastery));
    }

    private void writeEvent(Long userId, Long skillId, AiLearningEventTypeEnum type, Long refId, String meta) {
        learningEventMapper.insert(AiLearningEventDO.builder()
                .userId(userId)
                .skillId(skillId)
                .eventType(type.getType())
                .refId(refId)
                .scoreDelta(type.getXp())
                .meta(meta)
                .build());
    }

    private AiUserLearningProfileDO ensureProfile(Long userId) {
        AiUserLearningProfileDO profile = profileMapper.selectByUserId(userId);
        if (profile != null) {
            return profile;
        }
        profile = AiUserLearningProfileDO.builder()
                .userId(userId)
                .totalXp(0)
                .levelNo(1)
                .currentStreak(0)
                .longestStreak(0)
                .weeklyXp("{}")
                .build();
        profileMapper.insert(profile);
        return profile;
    }

    private AiLearningFlowRecordResult buildRecordResult(Long userId, Integer beforeXp, Integer mastery) {
        AiUserLearningProfileDO profile = ensureProfile(userId);
        AiLearningFlowRecordResult result = new AiLearningFlowRecordResult();
        result.setXpGained(defaultInt(profile.getTotalXp()) - defaultInt(beforeXp));
        result.setTotalXp(defaultInt(profile.getTotalXp()));
        result.setLevelNo(defaultInt(profile.getLevelNo()));
        result.setMastery(mastery);
        return result;
    }

    private boolean existsKnowledgePoint(Long userId, Long skillId, String normalizedContent) {
        return knowledgePointMapper.selectListByUserSkill(userId, skillId).stream()
                .map(AiKnowledgePointDO::getContent)
                .filter(StrUtil::isNotBlank)
                .map(this::normalizePoint)
                .anyMatch(normalizedContent::equals);
    }

    private DifyQuizJudgeRespDTO judgeQuizByDify(Long userId, AiQuizSubmitReqVO reqVO) {
        Map<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("mode", "quiz_judge");
        inputs.put("skillId", reqVO.getSkillId());
        inputs.put("chatId", reqVO.getChatId());
        inputs.put("question", reqVO.getQuestion());
        inputs.put("referenceAnswer", reqVO.getReferenceAnswer());
        inputs.put("userAnswer", reqVO.getUserAnswer());

        DifyChatMessageReqDTO difyReq = new DifyChatMessageReqDTO();
        difyReq.setInputs(inputs);
        difyReq.setResponseMode("blocking");
        difyReq.setUser("user-" + userId);
        difyReq.setQuery("请判定学生小测题答案是否正确，只输出 JSON，不要输出 Markdown。\n\n"
                + "题目：\n" + reqVO.getQuestion() + "\n\n"
                + "参考答案：\n" + reqVO.getReferenceAnswer() + "\n\n"
                + "学生答案：\n" + reqVO.getUserAnswer() + "\n\n"
                + "输出格式：{\"correct\":true,\"score\":85,\"feedback\":\"反馈内容\"}");

        DifyChatMessageRespDTO difyResp = difyChatflowClient.sendChatMessage(difyReq);
        String answer = difyResp == null ? null : difyResp.getAnswer();
        if (StrUtil.isBlank(answer)) {
            throw new IllegalStateException("Dify 判题结果为空");
        }
        try {
            DifyQuizJudgeRespDTO judge = objectMapper.readValue(extractJson(answer), DifyQuizJudgeRespDTO.class);
            if (judge.getCorrect() == null && judge.getScore() != null) {
                judge.setCorrect(judge.getScore() >= 60);
            }
            if (judge.getScore() == null) {
                judge.setScore(Boolean.TRUE.equals(judge.getCorrect()) ? 80 : 40);
            }
            if (judge.getCorrect() == null) {
                judge.setCorrect(judge.getScore() >= 60);
            }
            return judge;
        } catch (Exception ex) {
            throw new IllegalStateException("Dify 判题 JSON 解析失败：" + answer, ex);
        }
    }

    private AiReportRespVO buildGrowthReportByDify(Long userId) {
        Map<String, Object> inputs = buildGrowthReportInputs(userId);
        if (isReportDataEmpty(inputs)) {
            return emptyGrowthReport();
        }

        DifyChatMessageReqDTO difyReq = new DifyChatMessageReqDTO();
        difyReq.setInputs(inputs);
        difyReq.setResponseMode("blocking");
        difyReq.setUser("user-" + userId);
        difyReq.setQuery("请根据后端提供的真实学习数据生成成长报告。只输出 JSON，不要输出 Markdown。");

        DifyChatMessageRespDTO difyResp = difyChatflowClient.sendChatMessage(difyReq);
        String answer = difyResp == null ? null : difyResp.getAnswer();
        if (StrUtil.isBlank(answer)) {
            throw new IllegalStateException("Dify 成长报告结果为空");
        }
        try {
            return toReportResp(objectMapper.readValue(extractJson(answer), DifyGrowthReportRespDTO.class));
        } catch (Exception ex) {
            throw new IllegalStateException("Dify 成长报告 JSON 解析失败：" + answer, ex);
        }
    }

    private Map<String, Object> buildGrowthReportInputs(Long userId) {
        List<AiLearningEventDO> events = learningEventMapper.selectRecentList(userId, 200);
        List<AiSkillProgressDO> progresses = skillProgressMapper.selectListByUserId(userId);
        List<AiKnowledgePointDO> dueReviews = knowledgePointMapper.selectDueReviews(userId, LocalDateTime.now());

        Map<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("mode", "growth_report");
        inputs.put("period", "本周");
        inputs.put("eventStats", buildEventStats(events));
        inputs.put("skillMastery", buildSkillMastery(progresses));
        inputs.put("strongPoints", buildStrongPoints(progresses));
        inputs.put("weakPoints", buildWeakPoints(progresses, dueReviews));
        inputs.put("dueReviews", dueReviews.stream()
                .map(AiKnowledgePointDO::getContent)
                .filter(StrUtil::isNotBlank)
                .limit(10)
                .collect(Collectors.toList()));
        inputs.put("tutorMemory", buildReportTutorMemory(userId, progresses));
        inputs.put("learnerProfile", getProfile(userId));
        return inputs;
    }

    private Map<String, Long> buildEventStats(List<AiLearningEventDO> events) {
        return events.stream()
                .filter(item -> StrUtil.isNotBlank(item.getEventType()))
                .collect(Collectors.groupingBy(AiLearningEventDO::getEventType, LinkedHashMap::new, Collectors.counting()));
    }

    private List<Map<String, Object>> buildSkillMastery(List<AiSkillProgressDO> progresses) {
        return progresses.stream().map(item -> {
            Map<String, Object> map = new LinkedHashMap<>();
            AiSkillDO skill = skillMapper.selectById(item.getSkillId());
            map.put("skillId", item.getSkillId());
            map.put("skillName", skill == null ? "" : skill.getName());
            map.put("mastery", defaultInt(item.getProgress()));
            return map;
        }).collect(Collectors.toList());
    }

    private List<String> buildStrongPoints(List<AiSkillProgressDO> progresses) {
        return progresses.stream()
                .filter(item -> defaultInt(item.getProgress()) >= 80)
                .map(item -> {
                    AiSkillDO skill = skillMapper.selectById(item.getSkillId());
                    if (skill == null || StrUtil.isBlank(skill.getName())) {
                        return null;
                    }
                    return skill.getName() + "（掌握度 " + defaultInt(item.getProgress()) + "%）";
                })
                .filter(Objects::nonNull)
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<String> buildWeakPoints(List<AiSkillProgressDO> progresses, List<AiKnowledgePointDO> dueReviews) {
        List<String> weakPoints = new ArrayList<>();
        progresses.stream()
                .filter(item -> defaultInt(item.getProgress()) < 60)
                .forEach(item -> {
                    AiSkillDO skill = skillMapper.selectById(item.getSkillId());
                    if (skill != null && StrUtil.isNotBlank(skill.getName())) {
                        weakPoints.add(skill.getName() + "（掌握度 " + defaultInt(item.getProgress()) + "%）");
                    }
                });
        dueReviews.stream()
                .map(AiKnowledgePointDO::getContent)
                .filter(StrUtil::isNotBlank)
                .limit(5)
                .forEach(weakPoints::add);
        return weakPoints.stream().distinct().limit(8).collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildReportTutorMemory(Long userId, List<AiSkillProgressDO> progresses) {
        return progresses.stream()
                .map(item -> {
                    AiTutorMemoryDO memory = tutorMemoryMapper.selectByUserSkillTutor(userId, item.getSkillId(), DEFAULT_TUTOR);
                    if (memory == null) {
                        return null;
                    }
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("skillId", item.getSkillId());
                    map.put("taughtSummary", memory.getTaughtSummary());
                    map.put("stuckPoints", memory.getStuckPoints());
                    map.put("nextSuggestion", memory.getNextSuggestion());
                    return map;
                })
                .filter(Objects::nonNull)
                .limit(10)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private boolean isReportDataEmpty(Map<String, Object> inputs) {
        return ((Map<String, Long>) inputs.get("eventStats")).isEmpty()
                && ((List<?>) inputs.get("skillMastery")).isEmpty()
                && ((List<?>) inputs.get("strongPoints")).isEmpty()
                && ((List<?>) inputs.get("weakPoints")).isEmpty()
                && ((List<?>) inputs.get("dueReviews")).isEmpty();
    }

    private AiReportRespVO emptyGrowthReport() {
        AiReportRespVO respVO = new AiReportRespVO();
        respVO.setTitle("本周 AI 伴学成长报告");
        respVO.setSummary("本周期暂无足够学习记录，暂时无法生成完整成长分析。建议先完成几轮 AI 伴学对话，并完成至少一次知识点复习。");
        respVO.setStrongPoints(Collections.emptyList());
        respVO.setWeakPoints(Collections.emptyList());
        respVO.setNextPlan(Arrays.asList("先选择一个技能开始 AI 伴学对话", "完成对话后复习系统生成的知识点卡片"));
        respVO.setRiskReminder("暂无足够数据判断学习风险。");
        return respVO;
    }

    private AiReportRespVO toReportResp(DifyGrowthReportRespDTO report) {
        AiReportRespVO respVO = new AiReportRespVO();
        respVO.setTitle(StrUtil.blankToDefault(report.getTitle(), "本周 AI 伴学成长报告"));
        respVO.setSummary(StrUtil.blankToDefault(report.getSummary(), "暂无足够学习记录。"));
        respVO.setStrongPoints(listOrEmpty(report.getStrongPoints()));
        respVO.setWeakPoints(listOrEmpty(report.getWeakPoints()));
        respVO.setNextPlan(listOrEmpty(report.getNextPlan()));
        respVO.setRiskReminder(StrUtil.blankToDefault(report.getRiskReminder(), "暂无明显风险"));
        return respVO;
    }

    private List<String> listOrEmpty(List<String> value) {
        return value == null ? Collections.emptyList() : value;
    }

    private String extractJson(String text) {
        if (StrUtil.isBlank(text)) {
            return "{}";
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    private String buildQuizMeta(AiQuizSubmitReqVO reqVO, DifyQuizJudgeRespDTO judge, boolean correct) {
        try {
            Map<String, Object> meta = new LinkedHashMap<>();
            meta.put("question", reqVO.getQuestion());
            meta.put("referenceAnswer", reqVO.getReferenceAnswer());
            meta.put("userAnswer", reqVO.getUserAnswer());
            meta.put("correct", correct);
            meta.put("score", judge.getScore());
            meta.put("feedback", judge.getFeedback());
            return objectMapper.writeValueAsString(meta);
        } catch (Exception ignored) {
            return "{\"correct\":" + correct + "}";
        }
    }

    private DifyLearningResponseDTO fallback(String rawResponse) {
        DifyLearningResponseDTO dto = new DifyLearningResponseDTO();
        dto.setAnswer(StrUtil.blankToDefault(rawResponse, ""));
        return dto;
    }

    private String normalizePoint(String point) {
        return StrUtil.trim(point)
                .replaceAll("[\\s,，。.;；:：、]+", "")
                .toLowerCase();
    }

    private int computeLevel(int xp) {
        int level = 1;
        int threshold = 100;
        while (xp >= threshold) {
            level++;
            threshold += level * 100;
        }
        return level;
    }

    private int defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private String updateWeeklyXp(String weeklyXp, LocalDate date, int xp) {
        try {
            Map<String, Integer> map = StrUtil.isBlank(weeklyXp)
                    ? new LinkedHashMap<>()
                    : objectMapper.readValue(weeklyXp, objectMapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Integer.class));
            String key = date.toString();
            map.put(key, map.getOrDefault(key, 0) + xp);
            return objectMapper.writeValueAsString(map);
        } catch (Exception ignored) {
            return "{\"" + date + "\":" + xp + "}";
        }
    }

    private String merge(String oldValue, String newValue) {
        if (StrUtil.isBlank(newValue)) {
            return oldValue;
        }
        if (StrUtil.isBlank(oldValue)) {
            return newValue;
        }
        if (oldValue.contains(newValue)) {
            return oldValue;
        }
        return oldValue + TEXT_SEPARATOR + newValue;
    }

    private String limitText(String value) {
        if (value == null || value.length() <= MAX_MEMORY_LENGTH) {
            return value;
        }
        return value.substring(value.length() - MAX_MEMORY_LENGTH);
    }
}
