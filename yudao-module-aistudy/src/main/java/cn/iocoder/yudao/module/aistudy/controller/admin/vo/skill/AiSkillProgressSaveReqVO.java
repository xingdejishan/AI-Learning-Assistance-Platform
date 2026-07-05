package cn.iocoder.yudao.module.aistudy.controller.admin.vo.skill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 技能进度保存 Request VO")
@Data
public class AiSkillProgressSaveReqVO {
    private Long id;
    private Long userId;
    @NotNull(message = "技能编号不能为空")
    private Long skillId;
    private Integer progress;
    private Integer status;
    private Integer score;
}
