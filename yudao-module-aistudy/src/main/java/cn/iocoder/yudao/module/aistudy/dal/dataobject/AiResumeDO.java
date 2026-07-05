package cn.iocoder.yudao.module.aistudy.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("ai_resume")
@KeySequence("ai_resume_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResumeDO extends BaseDO {

    @TableId
    private Long id;
    private Long userId;
    private String fileUrl;
    private String fileName;
    private Integer status;
    private Integer score;
    private String analysisResult;
    private String optimizeSuggestion;

}
