package cn.iocoder.yudao.module.aistudy.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("ai_tutor_memory")
@KeySequence("ai_tutor_memory_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiTutorMemoryDO extends BaseDO {

    @TableId
    private Long id;
    private Long userId;
    private Long skillId;
    private String tutorCode;
    private String taughtSummary;
    private String stuckPoints;
    private String nextSuggestion;
    private String attitude;

}
