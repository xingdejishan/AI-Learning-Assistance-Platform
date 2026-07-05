package cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AiUserLearningProfileRespVO {
    private Integer totalXp;
    private Integer levelNo;
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDate lastActiveDate;
    private String weeklyXp;
}
