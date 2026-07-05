package cn.iocoder.yudao.module.aistudy.framework.dify;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DifyLearningResponseDTO {
    private String answer;
    private List<String> knowledgePoints = new ArrayList<>();
    private DifyQuizDTO quiz;
    private List<String> stuckPoints = new ArrayList<>();
    private String nextSuggestion;
    private DifyTutorMemoryUpdateDTO tutorMemoryUpdate;

    @Data
    public static class DifyQuizDTO {
        private String question;
        private String answer;
        private Integer difficulty;
    }

    @Data
    public static class DifyTutorMemoryUpdateDTO {
        private String taughtSummary;
        private String stuckPoints;
        private String nextSuggestion;
    }
}
