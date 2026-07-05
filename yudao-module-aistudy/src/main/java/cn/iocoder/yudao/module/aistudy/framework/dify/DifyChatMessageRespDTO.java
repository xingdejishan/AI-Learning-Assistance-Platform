package cn.iocoder.yudao.module.aistudy.framework.dify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class DifyChatMessageRespDTO {

    private String event;

    @JsonProperty("task_id")
    private String taskId;

    private String id;

    @JsonProperty("message_id")
    private String messageId;

    @JsonProperty("conversation_id")
    private String conversationId;

    private String mode;

    private String answer;

    private Map<String, Object> metadata;
}
