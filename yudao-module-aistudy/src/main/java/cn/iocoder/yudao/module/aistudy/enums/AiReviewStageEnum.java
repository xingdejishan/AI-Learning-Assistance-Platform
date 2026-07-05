package cn.iocoder.yudao.module.aistudy.enums;

public final class AiReviewStageEnum {

    private static final int[] HOURS = {0, 24, 72, 168, 336};

    private AiReviewStageEnum() {
    }

    public static Integer nextStage(Integer stage, Boolean remembered) {
        int value = stage == null ? 0 : stage;
        if (Boolean.TRUE.equals(remembered)) {
            return Math.min(value + 1, 5);
        }
        return Math.max(value - 1, 0);
    }

    public static Integer intervalHours(Integer stage) {
        if (stage == null || stage <= 0) {
            return 0;
        }
        if (stage >= 5) {
            return null;
        }
        return HOURS[stage];
    }
}
