package cn.iocoder.yudao.module.aistudy.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiKnowledgePointDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AiKnowledgePointMapper extends BaseMapperX<AiKnowledgePointDO> {

    default List<AiKnowledgePointDO> selectListByUserSkill(Long userId, Long skillId) {
        return selectList(new LambdaQueryWrapperX<AiKnowledgePointDO>()
                .eq(AiKnowledgePointDO::getUserId, userId)
                .eq(AiKnowledgePointDO::getSkillId, skillId)
                .orderByDesc(AiKnowledgePointDO::getId));
    }

    default List<AiKnowledgePointDO> selectDueReviews(Long userId, LocalDateTime now) {
        return selectList(new LambdaQueryWrapperX<AiKnowledgePointDO>()
                .eq(AiKnowledgePointDO::getUserId, userId)
                .le(AiKnowledgePointDO::getNextReviewTime, now)
                .lt(AiKnowledgePointDO::getStage, 5)
                .orderByAsc(AiKnowledgePointDO::getNextReviewTime));
    }

    default AiKnowledgePointDO selectByUserSkillContent(Long userId, Long skillId, String content) {
        return selectOne(new LambdaQueryWrapperX<AiKnowledgePointDO>()
                .eq(AiKnowledgePointDO::getUserId, userId)
                .eq(AiKnowledgePointDO::getSkillId, skillId)
                .eq(AiKnowledgePointDO::getContent, content));
    }

}
