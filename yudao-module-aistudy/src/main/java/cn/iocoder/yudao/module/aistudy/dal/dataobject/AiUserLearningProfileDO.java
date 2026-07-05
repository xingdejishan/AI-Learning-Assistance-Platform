package cn.iocoder.yudao.module.aistudy.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

@TableName("ai_user_learning_profile")
@KeySequence("ai_user_learning_profile_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiUserLearningProfileDO extends BaseDO {

    @TableId
    private Long id;
    private Long userId;
    private Integer totalXp;
    private Integer levelNo;
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDate lastActiveDate;
    private String weeklyXp;

}
