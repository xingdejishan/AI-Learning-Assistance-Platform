package cn.iocoder.yudao.module.aistudy.controller.admin.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 学习数据看板 Response VO")
@Data
public class AiDashboardRespVO {
    private Integer totalStudyDuration;
    private Long todayLearningRecordCount;
    private Long aiChatCount;
    private Long resumeDiagnosisCount;
    private Integer skillCompletionRate;
    private List<Map<String, Object>> studyDurationTrend;
    private List<Map<String, Object>> skillProgressDistribution;
    private List<Map<String, Object>> aiAndResumeTrend;
}
