package cn.iocoder.yudao.module.aistudy.controller.admin.vo.skill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 技能进度 Response VO")
@Data
public class AiSkillProgressRespVO {
    private Long id;
    private Long userId;
    private Long skillId;
    private Integer progress;
    private Integer status;
    private Integer score;
    private LocalDateTime lastStudyTime;
    private String recommendation;
}
