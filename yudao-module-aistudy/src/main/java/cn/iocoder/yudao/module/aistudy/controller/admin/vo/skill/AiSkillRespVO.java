package cn.iocoder.yudao.module.aistudy.controller.admin.vo.skill;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "管理后台 - 技能 Response VO")
@Data
public class AiSkillRespVO {
    private Long id;
    private String name;
    private String category;
    private Long parentId;
    private Integer level;
    private String description;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private List<AiSkillRespVO> children = new ArrayList<>();
}
