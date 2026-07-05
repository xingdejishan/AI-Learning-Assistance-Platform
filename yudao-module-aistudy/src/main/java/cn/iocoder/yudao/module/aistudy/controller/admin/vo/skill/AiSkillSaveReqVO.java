package cn.iocoder.yudao.module.aistudy.controller.admin.vo.skill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "管理后台 - 技能保存 Request VO")
@Data
public class AiSkillSaveReqVO {
    private Long id;
    @NotBlank(message = "技能名称不能为空")
    private String name;
    private String category;
    private Long parentId;
    private Integer level;
    private String description;
    private Integer sort;
    private Integer status;
}
