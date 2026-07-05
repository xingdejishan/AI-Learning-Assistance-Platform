package cn.iocoder.yudao.module.aistudy.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiChatRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiChatRecordMapper extends BaseMapperX<AiChatRecordDO> {

    default List<AiChatRecordDO> selectList(Long userId, String conversationId) {
        return selectList(new LambdaQueryWrapperX<AiChatRecordDO>()
                .eqIfPresent(AiChatRecordDO::getUserId, userId)
                .eqIfPresent(AiChatRecordDO::getConversationId, conversationId)
                .orderByDesc(AiChatRecordDO::getId));
    }

}
