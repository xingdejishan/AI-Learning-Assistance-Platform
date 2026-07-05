package cn.iocoder.yudao.module.aistudy.controller.admin.vo.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - AI 辅导回答 Response VO")
@Data
public class AiChatRespVO {

    private Long id;

    private String question;

    private String answer;

    private String conversationId;

    private String modelName;

    private String messageId;

    private List<String> knowledgePoints;

    private Object quiz;

    private Integer mastery;

    private Integer xpGained;

    private String nextSuggestion;
}
