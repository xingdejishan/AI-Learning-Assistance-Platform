package cn.iocoder.yudao.module.aistudy.framework.dify;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "dify.workflow")
public class DifyWorkflowProperties {
    private String apiUrl;
    private String apiKey;
    private String resumeWorkflowId;
}
