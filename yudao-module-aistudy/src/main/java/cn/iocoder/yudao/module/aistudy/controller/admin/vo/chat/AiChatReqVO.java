package cn.iocoder.yudao.module.aistudy.controller.admin.vo.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - AI 辅导提问 Request VO")
@Data
public class AiChatReqVO {

    private String question;

    private String message;

    private String conversationId;

    private Long skillId;

    private String tutorCode;
}
