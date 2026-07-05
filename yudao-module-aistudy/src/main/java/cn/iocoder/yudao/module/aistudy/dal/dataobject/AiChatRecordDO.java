package cn.iocoder.yudao.module.aistudy.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("ai_chat_record")
@KeySequence("ai_chat_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRecordDO extends BaseDO {

    @TableId
    private Long id;
    private Long userId;
    private String question;
    private String answer;
    private String conversationId;
    private String difyMessageId;
    private String rawResponse;
    private String parsedResponse;
    private String modelName;

}
