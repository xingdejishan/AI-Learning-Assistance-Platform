package cn.iocoder.yudao.module.aistudy.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiSkillProgressDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiSkillProgressMapper extends BaseMapperX<AiSkillProgressDO> {

    default AiSkillProgressDO selectByUserIdAndSkillId(Long userId, Long skillId) {
        return selectOne(new LambdaQueryWrapperX<AiSkillProgressDO>()
                .eq(AiSkillProgressDO::getUserId, userId)
                .eq(AiSkillProgressDO::getSkillId, skillId));
    }

    default List<AiSkillProgressDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<AiSkillProgressDO>()
                .eqIfPresent(AiSkillProgressDO::getUserId, userId)
                .orderByDesc(AiSkillProgressDO::getLastStudyTime));
    }

}
