package cn.iocoder.yudao.module.aistudy.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiTutorMemoryDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiTutorMemoryMapper extends BaseMapperX<AiTutorMemoryDO> {

    default AiTutorMemoryDO selectByUserSkillTutor(Long userId, Long skillId, String tutorCode) {
        return selectOne(new LambdaQueryWrapperX<AiTutorMemoryDO>()
                .eq(AiTutorMemoryDO::getUserId, userId)
                .eq(AiTutorMemoryDO::getSkillId, skillId)
                .eq(AiTutorMemoryDO::getTutorCode, tutorCode));
    }

}
