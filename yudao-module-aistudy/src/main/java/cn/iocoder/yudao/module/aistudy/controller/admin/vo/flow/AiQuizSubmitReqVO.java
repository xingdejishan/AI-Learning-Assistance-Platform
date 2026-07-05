package cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AiQuizSubmitReqVO {

    @NotNull(message = "技能 ID 不能为空")
    private Long skillId;

    private Long chatId;

    @NotBlank(message = "题目不能为空")
    @Size(max = 1000, message = "题目不能超过 1000 字")
    private String question;

    @NotBlank(message = "参考答案不能为空")
    @Size(max = 2000, message = "参考答案不能超过 2000 字")
    private String referenceAnswer;

    @NotBlank(message = "学生答案不能为空")
    @Size(max = 2000, message = "学生答案不能超过 2000 字")
    private String userAnswer;
}
