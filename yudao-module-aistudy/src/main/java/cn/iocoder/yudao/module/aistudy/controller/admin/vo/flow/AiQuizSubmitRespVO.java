package cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow;

import lombok.Data;

@Data
public class AiQuizSubmitRespVO {

    private Boolean correct;

    private Integer score;

    private String feedback;

    private Integer xpGained;

    private Integer mastery;

    private Integer totalXp;

    private Integer levelNo;
}
