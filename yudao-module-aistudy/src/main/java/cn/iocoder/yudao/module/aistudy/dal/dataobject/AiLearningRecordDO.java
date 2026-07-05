package cn.iocoder.yudao.module.aistudy.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

@TableName("ai_learning_record")
@KeySequence("ai_learning_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiLearningRecordDO extends BaseDO {

    @TableId
    private Long id;
    private Long userId;
    private Long skillId;
    private String title;
    private Integer duration;
    private LocalDate studyDate;
    private String content;

}
