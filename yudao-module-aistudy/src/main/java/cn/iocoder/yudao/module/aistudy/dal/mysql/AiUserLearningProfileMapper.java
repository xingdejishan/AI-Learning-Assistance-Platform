package cn.iocoder.yudao.module.aistudy.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiUserLearningProfileDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiUserLearningProfileMapper extends BaseMapperX<AiUserLearningProfileDO> {

    default AiUserLearningProfileDO selectByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<AiUserLearningProfileDO>()
                .eq(AiUserLearningProfileDO::getUserId, userId));
    }

}
