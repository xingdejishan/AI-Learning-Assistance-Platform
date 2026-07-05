package cn.iocoder.yudao.module.aistudy.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("ai_learning_event")
@KeySequence("ai_learning_event_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiLearningEventDO extends BaseDO {

    @TableId
    private Long id;
    private Long userId;
    private Long skillId;
    private String eventType;
    private Long refId;
    private Integer scoreDelta;
    private String meta;

}
