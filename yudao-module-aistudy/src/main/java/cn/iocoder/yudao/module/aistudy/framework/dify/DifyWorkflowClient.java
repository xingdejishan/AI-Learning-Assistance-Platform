package cn.iocoder.yudao.module.aistudy.framework.dify;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
public class DifyWorkflowClient {

    private final DifyWorkflowProperties properties;

    public DifyWorkflowClient(DifyWorkflowProperties properties) {
        this.properties = properties;
    }

    public Result diagnoseResume(String fileUrl, String fileName) {
        if (StrUtil.isBlank(properties.getApiUrl()) || StrUtil.isBlank(properties.getApiKey())) {
            return new Result(75, "项目经历描述较完整，但量化成果不足。", "建议补充技术栈、职责范围、性能指标和项目成果数据。");
        }
        return new Result(75, "Dify Workflow 已配置，真实调用将在下一阶段接入。", "请完善 Workflow 入参和结果映射。");
    }

    @Data
    @AllArgsConstructor
    public static class Result {
        private Integer score;
        private String analysisResult;
        private String optimizeSuggestion;
    }
}
