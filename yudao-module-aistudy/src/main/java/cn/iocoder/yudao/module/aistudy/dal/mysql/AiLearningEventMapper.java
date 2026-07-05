package cn.iocoder.yudao.module.aistudy.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiLearningEventDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiLearningEventMapper extends BaseMapperX<AiLearningEventDO> {

    default Long selectCountByUserSkillAndType(Long userId, Long skillId, String eventType) {
        return selectCount(new LambdaQueryWrapperX<AiLearningEventDO>()
                .eq(AiLearningEventDO::getUserId, userId)
                .eqIfPresent(AiLearningEventDO::getSkillId, skillId)
                .eq(AiLearningEventDO::getEventType, eventType));
    }

    default List<AiLearningEventDO> selectRecentList(Long userId, Integer limit) {
        return selectList(new LambdaQueryWrapperX<AiLearningEventDO>()
                .eq(AiLearningEventDO::getUserId, userId)
                .orderByDesc(AiLearningEventDO::getId)
                .last("LIMIT " + (limit == null ? 20 : limit)));
    }

}
