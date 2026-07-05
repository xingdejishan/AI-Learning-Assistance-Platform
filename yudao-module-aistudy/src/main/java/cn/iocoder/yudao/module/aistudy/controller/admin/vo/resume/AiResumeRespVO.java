package cn.iocoder.yudao.module.aistudy.controller.admin.vo.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 简历诊断 Response VO")
@Data
public class AiResumeRespVO {
    private Long id;
    private Long userId;
    private String fileUrl;
    private String fileName;
    private Integer status;
    private Integer score;
    private String analysisResult;
    private String optimizeSuggestion;
    private LocalDateTime createTime;
}
