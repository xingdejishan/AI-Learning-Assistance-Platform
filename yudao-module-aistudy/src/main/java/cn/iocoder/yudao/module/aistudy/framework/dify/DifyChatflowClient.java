package cn.iocoder.yudao.module.aistudy.framework.dify;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class DifyChatflowClient {

    private final DifyChatflowProperties properties;

    public DifyChatflowClient(DifyChatflowProperties properties) {
        this.properties = properties;
    }

    public DifyChatMessageRespDTO sendChatMessage(DifyChatMessageReqDTO req) {
        if (StrUtil.isBlank(properties.getChatApiKey())) {
            throw new DifyChatflowException("Dify Chatflow API Key 未配置");
        }
        String baseUrl = StrUtil.removeSuffix(StrUtil.blankToDefault(properties.getBaseUrl(), "https://api.dify.ai/v1"), "/");
        String url = baseUrl + "/chat-messages";

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        int timeoutMillis = (properties.getTimeoutSeconds() == null ? 60 : properties.getTimeoutSeconds()) * 1000;
        factory.setConnectTimeout(timeoutMillis);
        factory.setReadTimeout(timeoutMillis);
        RestTemplate restTemplate = new RestTemplate(factory);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getChatApiKey());

        try {
            ResponseEntity<DifyChatMessageRespDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, new HttpEntity<>(req, headers), DifyChatMessageRespDTO.class);
            DifyChatMessageRespDTO body = response.getBody();
            if (body == null || StrUtil.isBlank(body.getAnswer())) {
                throw new DifyChatflowException("Dify 返回内容为空");
            }
            return body;
        } catch (HttpStatusCodeException ex) {
            log.error("[sendChatMessage][Dify 调用失败][status({}) body({})]", ex.getRawStatusCode(), brief(ex.getResponseBodyAsString()));
            throw new DifyChatflowException("Dify 调用失败，HTTP " + ex.getRawStatusCode(), ex);
        } catch (ResourceAccessException ex) {
            log.error("[sendChatMessage][Dify 调用超时或网络不可达]");
            throw new DifyChatflowException("Dify 调用超时或网络不可达", ex);
        } catch (DifyChatflowException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("[sendChatMessage][Dify 调用异常][type({})]", ex.getClass().getSimpleName());
            throw new DifyChatflowException("Dify 调用异常", ex);
        }
    }

    public Result chat(String question, String conversationId) {
        DifyChatMessageReqDTO req = new DifyChatMessageReqDTO();
        req.setQuery(question);
        req.setConversationId(conversationId);
        req.setResponseMode(StrUtil.blankToDefault(properties.getResponseMode(), "blocking"));
        req.setUser("compat-user");
        DifyChatMessageRespDTO resp = sendChatMessage(req);
        return new Result(resp.getAnswer(), resp.getConversationId(), "dify-chatflow", resp.getMessageId(), resp);
    }

    private String brief(String value) {
        if (value == null) {
            return "";
        }
        return value.length() > 500 ? value.substring(0, 500) : value;
    }

    @Data
    @AllArgsConstructor
    public static class Result {
        private String answer;
        private String conversationId;
        private String modelName;
        private String messageId;
        private DifyChatMessageRespDTO rawResponse;
    }
}
