package cn.iocoder.yudao.module.aistudy.controller.admin.vo.learning;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 学习记录 Response VO")
@Data
public class AiLearningRecordRespVO {
    private Long id;
    private Long userId;
    private Long skillId;
    private String title;
    private Integer duration;
    private LocalDate studyDate;
    private String content;
    private LocalDateTime createTime;
}
