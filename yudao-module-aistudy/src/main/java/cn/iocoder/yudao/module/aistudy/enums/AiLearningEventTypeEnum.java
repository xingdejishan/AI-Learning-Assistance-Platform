package cn.iocoder.yudao.module.aistudy.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiLearningEventTypeEnum {
    CHAT("CHAT", 5),
    KP_ADD("KP_ADD", 20),
    REVIEW("REVIEW", 15),
    QUIZ_CORRECT("QUIZ_CORRECT", 10),
    QUIZ_WRONG("QUIZ_WRONG", 0),
    DURATION("DURATION", 0),
    REPORT_GENERATE("REPORT_GENERATE", 0);

    private final String type;
    private final Integer xp;
}
