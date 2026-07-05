package cn.iocoder.yudao.module.aistudy.service;

import cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow.*;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.*;
import cn.iocoder.yudao.module.aistudy.framework.dify.DifyLearningResponseDTO;

import java.util.List;

public interface AiLearningFlowService {

    DifyLearningResponseDTO parseLearningResponse(String rawResponse);

    AiLearningFlowRecordResult recordChatLearning(Long userId, Long skillId, Long chatId, DifyLearningResponseDTO learningResponse, String tutorCode);

    List<AiKnowledgePointDO> addKnowledgePoints(Long userId, Long skillId, Long chatId, List<String> points, String tutorCode);

    AiReviewSubmitRespVO reviewKnowledgePoint(Long userId, Long knowledgePointId, Boolean remembered);

    AiQuizSubmitRespVO submitQuizAnswer(Long userId, AiQuizSubmitReqVO reqVO);

    Integer recomputeSkillMastery(Long userId, Long skillId);

    List<AiKnowledgePointRespVO> getTodayReviews(Long userId);

    AiUserLearningProfileDO earnXp(Long userId, Integer amount, String source, Long skillId, Long refId);

    String buildTutorMemoryPrompt(Long userId, Long skillId, String tutorCode);

    void updateTutorMemory(Long userId, Long skillId, String tutorCode, String taughtSummary, String stuckPoints, String nextSuggestion);

    AiUserLearningProfileRespVO getProfile(Long userId);

    AiLearningFlowSummaryRespVO getSummary(Long userId);

    AiReportRespVO generateReport(Long userId);
}
