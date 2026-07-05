package cn.iocoder.yudao.module.aistudy.service;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.chat.*;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.dashboard.AiDashboardRespVO;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.learning.*;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.resume.AiResumeRespVO;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.skill.*;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.*;
import cn.iocoder.yudao.module.aistudy.dal.mysql.*;
import cn.iocoder.yudao.module.aistudy.framework.dify.*;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class AiStudyService {

    @Resource
    private AiSkillMapper skillMapper;
    @Resource
    private AiSkillProgressMapper skillProgressMapper;
    @Resource
    private AiLearningRecordMapper learningRecordMapper;
    @Resource
    private AiChatRecordMapper chatRecordMapper;
    @Resource
    private AiResumeMapper resumeMapper;
    @Resource
    private AiKnowledgePointMapper knowledgePointMapper;
    @Resource
    private DifyChatflowClient difyChatflowClient;
    @Resource
    private DifyWorkflowClient difyWorkflowClient;
    @Resource
    private AiLearningFlowService aiLearningFlowService;
    @Resource
    private DifyLearningResponseParser difyLearningResponseParser;
    @Resource
    private ObjectMapper objectMapper;

    public Long createSkill(AiSkillSaveReqVO reqVO) {
        AiSkillDO skill = BeanUtils.toBean(reqVO, AiSkillDO.class);
        fillSkillDefaults(skill);
        skillMapper.insert(skill);
        return skill.getId();
    }

    public void updateSkill(AiSkillSaveReqVO reqVO) {
        AiSkillDO skill = BeanUtils.toBean(reqVO, AiSkillDO.class);
        fillSkillDefaults(skill);
        skillMapper.updateById(skill);
    }

    public void deleteSkill(Long id) {
        skillMapper.deleteById(id);
    }

    public List<AiSkillRespVO> getSkillList(String name, String category, Integer status) {
        return BeanUtils.toBean(skillMapper.selectList(name, category, status), AiSkillRespVO.class);
    }

    public List<AiSkillRespVO> getSkillTree() {
        List<AiSkillRespVO> list = getSkillList(null, null, null);
        Map<Long, AiSkillRespVO> idMap = list.stream().collect(Collectors.toMap(AiSkillRespVO::getId, item -> item, (a, b) -> a));
        List<AiSkillRespVO> roots = new ArrayList<>();
        for (AiSkillRespVO item : list) {
            Long parentId = item.getParentId() == null ? 0L : item.getParentId();
            if (parentId == 0 || !idMap.containsKey(parentId)) {
                roots.add(item);
            } else {
                idMap.get(parentId).getChildren().add(item);
            }
        }
        return roots;
    }

    public List<AiSkillProgressRespVO> getSkillProgressList(Long userId) {
        Long queryUserId = userId == null ? SecurityFrameworkUtils.getLoginUserId() : userId;
        List<AiSkillProgressRespVO> list = BeanUtils.toBean(skillProgressMapper.selectListByUserId(queryUserId), AiSkillProgressRespVO.class);
        list.forEach(item -> item.setRecommendation(evaluateProgress(item.getProgress())));
        return list;
    }

    public Long updateSkillProgress(AiSkillProgressSaveReqVO reqVO) {
        Long userId = reqVO.getUserId() == null ? SecurityFrameworkUtils.getLoginUserId() : reqVO.getUserId();
        AiSkillProgressDO progress = skillProgressMapper.selectByUserIdAndSkillId(userId, reqVO.getSkillId());
        if (progress == null) {
            progress = BeanUtils.toBean(reqVO, AiSkillProgressDO.class);
            progress.setUserId(userId);
            progress.setProgress(defaultInt(progress.getProgress()));
            progress.setStatus(statusByProgress(progress.getProgress()));
            progress.setLastStudyTime(LocalDateTime.now());
            skillProgressMapper.insert(progress);
        } else {
            progress.setProgress(defaultInt(reqVO.getProgress()));
            progress.setScore(reqVO.getScore());
            progress.setStatus(statusByProgress(progress.getProgress()));
            progress.setLastStudyTime(LocalDateTime.now());
            skillProgressMapper.updateById(progress);
        }
        return progress.getId();
    }

    public String evaluateProgress(Integer progress) {
        int value = defaultInt(progress);
        if (value < 60) {
            return "\u5efa\u8bae\u7ee7\u7eed\u5b66\u4e60";
        }
        if (value < 80) {
            return "\u5efa\u8bae\u5f3a\u5316\u7ec3\u4e60";
        }
        return "\u5df2\u57fa\u672c\u638c\u63e1";
    }

    public Long createLearningRecord(AiLearningRecordSaveReqVO reqVO) {
        AiLearningRecordDO record = BeanUtils.toBean(reqVO, AiLearningRecordDO.class);
        record.setUserId(record.getUserId() == null ? SecurityFrameworkUtils.getLoginUserId() : record.getUserId());
        record.setDuration(defaultInt(record.getDuration()));
        record.setStudyDate(record.getStudyDate() == null ? LocalDate.now() : record.getStudyDate());
        learningRecordMapper.insert(record);
        return record.getId();
    }

    public void updateLearningRecord(AiLearningRecordSaveReqVO reqVO) {
        AiLearningRecordDO record = BeanUtils.toBean(reqVO, AiLearningRecordDO.class);
        record.setDuration(defaultInt(record.getDuration()));
        learningRecordMapper.updateById(record);
    }

    public void deleteLearningRecord(Long id) {
        learningRecordMapper.deleteById(id);
    }

    public PageResult<AiLearningRecordRespVO> getLearningRecordPage(AiLearningRecordPageReqVO reqVO) {
        PageResult<AiLearningRecordDO> page = learningRecordMapper.selectPage(reqVO);
        return BeanUtils.toBean(page, AiLearningRecordRespVO.class);
    }

    public List<Map<String, Object>> getDurationByUser() {
        return learningRecordMapper.selectDurationByUser();
    }

    public List<Map<String, Object>> getDurationBySkill() {
        return learningRecordMapper.selectDurationBySkill();
    }

    public AiChatRespVO chat(AiChatReqVO reqVO) {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        String message = StrUtil.blankToDefault(reqVO.getMessage(), reqVO.getQuestion());
        if (StrUtil.isBlank(message)) {
            throw new IllegalArgumentException("message is blank");
        }

        AiSkillDO skill = reqVO.getSkillId() == null ? null : skillMapper.selectById(reqVO.getSkillId());
        AiSkillProgressDO progress = reqVO.getSkillId() == null ? null :
                skillProgressMapper.selectByUserIdAndSkillId(userId, reqVO.getSkillId());
        List<AiKnowledgePointDO> learnedPoints = reqVO.getSkillId() == null ? Collections.emptyList() :
                knowledgePointMapper.selectListByUserSkill(userId, reqVO.getSkillId());
        List<AiKnowledgePointDO> duePoints = knowledgePointMapper.selectDueReviews(userId, LocalDateTime.now())
                .stream()
                .filter(item -> reqVO.getSkillId() == null || Objects.equals(item.getSkillId(), reqVO.getSkillId()))
                .collect(Collectors.toList());

        Map<String, Object> inputs = new LinkedHashMap<>();
        inputs.put("skillName", skill == null ? "" : skill.getName());
        inputs.put("mastery", progress == null ? 0 : defaultInt(progress.getProgress()));
        inputs.put("learnedKnowledgePoints", learnedPoints.stream().map(AiKnowledgePointDO::getContent).collect(Collectors.joining("\uFF1B")));
        inputs.put("dueReviewPoints", duePoints.stream().map(AiKnowledgePointDO::getContent).collect(Collectors.joining("\uFF1B")));
        inputs.put("learnerProfile", aiLearningFlowService.getProfile(userId));
        inputs.put("tutorMemory", reqVO.getSkillId() == null ? "" :
                aiLearningFlowService.buildTutorMemoryPrompt(userId, reqVO.getSkillId(), reqVO.getTutorCode()));
        inputs.put("query", message);

        DifyChatMessageReqDTO difyReq = new DifyChatMessageReqDTO();
        difyReq.setInputs(inputs);
        difyReq.setQuery(message);
        difyReq.setResponseMode("blocking");
        difyReq.setConversationId(StrUtil.blankToDefault(reqVO.getConversationId(), ""));
        difyReq.setUser("user-" + userId);
        DifyChatMessageRespDTO difyResp = difyChatflowClient.sendChatMessage(difyReq);
        String rawAnswer = difyResp.getAnswer();
        DifyLearningResponseDTO learning = difyLearningResponseParser.parseOrFallback(rawAnswer);
        String answer = StrUtil.blankToDefault(learning.getAnswer(), rawAnswer);
        String rawResponse = toJson(difyResp);
        String parsedResponse = toJson(learning);

        AiChatRecordDO record = AiChatRecordDO.builder()
                .userId(userId)
                .question(message)
                .answer(answer)
                .conversationId(difyResp.getConversationId())
                .difyMessageId(StrUtil.blankToDefault(difyResp.getMessageId(), difyResp.getId()))
                .rawResponse(rawResponse)
                .parsedResponse(parsedResponse)
                .modelName("dify-chatflow")
                .build();
        chatRecordMapper.insert(record);
        AiLearningFlowRecordResult recordResult = aiLearningFlowService.recordChatLearning(userId, reqVO.getSkillId(), record.getId(), learning, reqVO.getTutorCode());

        AiChatRespVO respVO = BeanUtils.toBean(record, AiChatRespVO.class);
        respVO.setId(record.getId());
        respVO.setMessageId(record.getDifyMessageId());
        respVO.setKnowledgePoints(learning.getKnowledgePoints());
        respVO.setQuiz(learning.getQuiz());
        respVO.setNextSuggestion(learning.getNextSuggestion());
        respVO.setXpGained(recordResult.getXpGained());
        respVO.setTotalXp(recordResult.getTotalXp());
        respVO.setLevelNo(recordResult.getLevelNo());
        respVO.setMastery(recordResult.getMastery());
        return respVO;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ignored) {
            return "";
        }
    }

    public List<AiChatRecordRespVO> getChatHistory(Long userId, String conversationId) {
        Long queryUserId = userId == null ? SecurityFrameworkUtils.getLoginUserId() : userId;
        return BeanUtils.toBean(chatRecordMapper.selectList(queryUserId, conversationId), AiChatRecordRespVO.class);
    }

    public AiResumeRespVO diagnoseResume(String fileUrl, String fileName, Long userId) {
        Long ownerId = userId == null ? SecurityFrameworkUtils.getLoginUserId() : userId;
        DifyWorkflowClient.Result result = difyWorkflowClient.diagnoseResume(fileUrl, fileName);
        AiResumeDO resume = AiResumeDO.builder()
                .userId(ownerId)
                .fileUrl(fileUrl)
                .fileName(fileName)
                .status(1)
                .score(result.getScore())
                .analysisResult(result.getAnalysisResult())
                .optimizeSuggestion(result.getOptimizeSuggestion())
                .build();
        resumeMapper.insert(resume);
        return BeanUtils.toBean(resume, AiResumeRespVO.class);
    }

    public List<AiResumeRespVO> getResumeList(Long userId) {
        Long queryUserId = userId == null ? SecurityFrameworkUtils.getLoginUserId() : userId;
        return BeanUtils.toBean(resumeMapper.selectList(queryUserId), AiResumeRespVO.class);
    }

    public AiResumeRespVO getResume(Long id) {
        return BeanUtils.toBean(resumeMapper.selectById(id), AiResumeRespVO.class);
    }

    public AiDashboardRespVO getDashboard() {
        AiDashboardRespVO respVO = new AiDashboardRespVO();
        List<AiLearningRecordDO> records = learningRecordMapper.selectList();
        List<AiChatRecordDO> chats = chatRecordMapper.selectList();
        List<AiResumeDO> resumes = resumeMapper.selectList();
        List<AiSkillProgressDO> progresses = skillProgressMapper.selectList();
        respVO.setTotalStudyDuration(records.stream().mapToInt(item -> defaultInt(item.getDuration())).sum());
        respVO.setTodayLearningRecordCount(records.stream().filter(item -> LocalDate.now().equals(item.getStudyDate())).count());
        respVO.setAiChatCount((long) chats.size());
        respVO.setResumeDiagnosisCount((long) resumes.size());
        respVO.setSkillCompletionRate(progresses.isEmpty() ? 0 : (int) progresses.stream().mapToInt(item -> defaultInt(item.getProgress())).average().orElse(0));
        respVO.setStudyDurationTrend(mockTrend("duration"));
        respVO.setSkillProgressDistribution(mockDistribution());
        respVO.setAiAndResumeTrend(mockTrend("count"));
        return respVO;
    }

    private void fillSkillDefaults(AiSkillDO skill) {
        skill.setParentId(skill.getParentId() == null ? 0L : skill.getParentId());
        skill.setLevel(skill.getLevel() == null ? 1 : skill.getLevel());
        skill.setSort(skill.getSort() == null ? 0 : skill.getSort());
        skill.setStatus(skill.getStatus() == null ? 0 : skill.getStatus());
    }

    private int defaultInt(Integer value) {
        return value == null ? 0 : value;
    }

    private int statusByProgress(Integer progress) {
        return defaultInt(progress) >= 80 ? 1 : 0;
    }

    private List<Map<String, Object>> mockTrend(String valueKey) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", LocalDate.now().minusDays(i).toString());
            item.put(valueKey, (7 - i) * 12);
            list.add(item);
        }
        return list;
    }

    private List<Map<String, Object>> mockDistribution() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(distributionItem("\u672a\u5f00\u59cb", 20));
        list.add(distributionItem("\u5b66\u4e60\u4e2d", 55));
        list.add(distributionItem("\u5df2\u638c\u63e1", 25));
        return list;
    }

    private Map<String, Object> distributionItem(String name, Integer value) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("name", name);
        item.put("value", value);
        return item;
    }
}
