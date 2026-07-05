package cn.iocoder.yudao.module.aistudy.framework.dify;

import lombok.Data;

@Data
public class DifyQuizJudgeRespDTO {

    private Boolean correct;

    private Integer score;

    private String feedback;
}
