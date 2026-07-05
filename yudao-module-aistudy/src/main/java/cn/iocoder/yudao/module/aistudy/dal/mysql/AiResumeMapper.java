package cn.iocoder.yudao.module.aistudy.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiResumeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiResumeMapper extends BaseMapperX<AiResumeDO> {

    default List<AiResumeDO> selectList(Long userId) {
        return selectList(new LambdaQueryWrapperX<AiResumeDO>()
                .eqIfPresent(AiResumeDO::getUserId, userId)
                .orderByDesc(AiResumeDO::getId));
    }

}
