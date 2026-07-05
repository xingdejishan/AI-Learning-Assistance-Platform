package cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiKnowledgePointRespVO {
    private Long id;
    private Long userId;
    private Long skillId;
    private String skillName;
    private String content;
    private Integer stage;
    private LocalDateTime nextReviewTime;
    private LocalDateTime lastReviewTime;
    private String taughtBy;
}
