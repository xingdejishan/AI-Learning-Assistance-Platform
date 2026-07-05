package cn.iocoder.yudao.module.aistudy.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("ai_knowledge_point")
@KeySequence("ai_knowledge_point_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiKnowledgePointDO extends BaseDO {

    @TableId
    private Long id;
    private Long userId;
    private Long skillId;
    private String content;
    private Long sourceChatId;
    private Integer stage;
    private LocalDateTime nextReviewTime;
    private LocalDateTime lastReviewTime;
    private String taughtBy;
    private String batchId;

}
