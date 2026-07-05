package cn.iocoder.yudao.module.aistudy.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.learning.AiLearningRecordPageReqVO;
import cn.iocoder.yudao.module.aistudy.dal.dataobject.AiLearningRecordDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AiLearningRecordMapper extends BaseMapperX<AiLearningRecordDO> {

    default PageResult<AiLearningRecordDO> selectPage(AiLearningRecordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiLearningRecordDO>()
                .eqIfPresent(AiLearningRecordDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiLearningRecordDO::getSkillId, reqVO.getSkillId())
                .likeIfPresent(AiLearningRecordDO::getTitle, reqVO.getTitle())
                .betweenIfPresent(AiLearningRecordDO::getStudyDate, reqVO.getStudyDate())
                .orderByDesc(AiLearningRecordDO::getStudyDate)
                .orderByDesc(AiLearningRecordDO::getId));
    }

    @Select("SELECT user_id AS userId, SUM(duration) AS duration FROM ai_learning_record WHERE deleted = b'0' GROUP BY user_id")
    List<Map<String, Object>> selectDurationByUser();

    @Select("SELECT skill_id AS skillId, SUM(duration) AS duration FROM ai_learning_record WHERE deleted = b'0' AND skill_id IS NOT NULL GROUP BY skill_id")
    List<Map<String, Object>> selectDurationBySkill();

}
