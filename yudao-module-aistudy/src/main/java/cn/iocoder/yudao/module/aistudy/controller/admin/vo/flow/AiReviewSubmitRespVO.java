package cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AiReviewSubmitRespVO {
    private Integer newStage;
    private LocalDateTime nextReviewTime;
    private Integer mastery;
    private Integer xpGained;
}
