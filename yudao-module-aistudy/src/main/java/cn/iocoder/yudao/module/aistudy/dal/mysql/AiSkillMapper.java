package cn.iocoder.yudao.module.aistudy.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiSkillDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiSkillMapper extends BaseMapperX<AiSkillDO> {

    default List<AiSkillDO> selectList(String name, String category, Integer status) {
        return selectList(new LambdaQueryWrapperX<AiSkillDO>()
                .likeIfPresent(AiSkillDO::getName, name)
                .eqIfPresent(AiSkillDO::getCategory, category)
                .eqIfPresent(AiSkillDO::getStatus, status)
                .orderByAsc(AiSkillDO::getSort)
                .orderByAsc(AiSkillDO::getId));
    }

}
