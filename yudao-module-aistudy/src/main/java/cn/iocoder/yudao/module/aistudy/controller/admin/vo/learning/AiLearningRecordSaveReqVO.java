package cn.iocoder.yudao.module.aistudy.controller.admin.vo.learning;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Schema(description = "管理后台 - 学习记录保存 Request VO")
@Data
public class AiLearningRecordSaveReqVO {
    private Long id;
    private Long userId;
    private Long skillId;
    @NotBlank(message = "标题不能为空")
    private String title;
    private Integer duration;
    private LocalDate studyDate;
    private String content;
}
