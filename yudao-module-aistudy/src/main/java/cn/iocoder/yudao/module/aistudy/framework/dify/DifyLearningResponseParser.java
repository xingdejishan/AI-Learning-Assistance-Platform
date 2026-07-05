package cn.iocoder.yudao.module.aistudy.framework.dify;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DifyLearningResponseParser {

    @Resource
    private ObjectMapper objectMapper;

    public DifyLearningResponseDTO parseOrFallback(String rawAnswer) {
        if (StrUtil.isBlank(rawAnswer)) {
            return fallback(rawAnswer);
        }
        DifyLearningResponseDTO direct = parse(rawAnswer);
        if (direct != null) {
            return direct;
        }
        int start = rawAnswer.indexOf('{');
        int end = rawAnswer.lastIndexOf('}');
        if (start >= 0 && end > start) {
            DifyLearningResponseDTO clipped = parse(rawAnswer.substring(start, end + 1));
            if (clipped != null) {
                return clipped;
            }
        }
        return fallback(rawAnswer);
    }

    private DifyLearningResponseDTO parse(String text) {
        try {
            return objectMapper.readValue(text, DifyLearningResponseDTO.class);
        } catch (Exception ignored) {
            return null;
        }
    }

    private DifyLearningResponseDTO fallback(String rawAnswer) {
        DifyLearningResponseDTO dto = new DifyLearningResponseDTO();
        dto.setAnswer(StrUtil.blankToDefault(rawAnswer, ""));
        return dto;
    }
}
