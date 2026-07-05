package cn.iocoder.yudao.module.aistudy.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("ai_skill_progress")
@KeySequence("ai_skill_progress_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiSkillProgressDO extends BaseDO {

    @TableId
    private Long id;
    private Long userId;
    private Long skillId;
    private Integer progress;
    private Integer status;
    private Integer score;
    private LocalDateTime lastStudyTime;

}
