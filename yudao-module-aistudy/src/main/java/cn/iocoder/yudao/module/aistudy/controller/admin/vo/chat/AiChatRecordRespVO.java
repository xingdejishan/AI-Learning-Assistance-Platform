package cn.iocoder.yudao.module.aistudy.controller.admin.vo.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 辅导记录 Response VO")
@Data
public class AiChatRecordRespVO {
    private Long id;
    private Long userId;
    private String question;
    private String answer;
    private String conversationId;
    private String modelName;
    private LocalDateTime createTime;
}
