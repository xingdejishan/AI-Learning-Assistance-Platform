package cn.iocoder.yudao.module.aistudy.framework.dify;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class DifyChatMessageReqDTO {

    private Map<String, Object> inputs;

    private String query;

    @JsonProperty("response_mode")
    private String responseMode;

    @JsonProperty("conversation_id")
    private String conversationId;

    private String user;
}
