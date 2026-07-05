package cn.iocoder.yudao.module.aistudy.framework.dify;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aistudy.dify")
public class DifyChatflowProperties {

    private String baseUrl = "https://api.dify.ai/v1";

    private String chatApiKey;

    private String responseMode = "blocking";

    private Integer timeoutSeconds = 60;
}
