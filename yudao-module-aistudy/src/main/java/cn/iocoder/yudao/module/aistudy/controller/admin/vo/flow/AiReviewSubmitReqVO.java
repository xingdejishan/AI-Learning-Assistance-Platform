package cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AiReviewSubmitReqVO {
    @NotNull
    private Long knowledgePointId;
    @NotNull
    private Boolean remembered;
}
