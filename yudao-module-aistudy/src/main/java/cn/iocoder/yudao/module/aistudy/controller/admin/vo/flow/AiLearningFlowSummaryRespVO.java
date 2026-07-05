package cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AiLearningFlowSummaryRespVO {
    private AiUserLearningProfileRespVO profile;
    private Integer todayReviewCount;
    private List<Map<String, Object>> masteryRanking;
    private List<Map<String, Object>> stageDistribution;
    private List<Map<String, Object>> recentEvents;
}
