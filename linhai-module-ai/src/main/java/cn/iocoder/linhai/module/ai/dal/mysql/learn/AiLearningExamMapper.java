package cn.iocoder.linhai.module.ai.dal.mysql.learn;

import cn.iocoder.linhai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.linhai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningExamDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 学习考试 Mapper
 *
 * @author qzt
 */
@Mapper
public interface AiLearningExamMapper extends BaseMapperX<AiLearningExamDO> {

    default List<AiLearningExamDO> selectListByPhaseId(Long phaseId) {
        return selectList(new LambdaQueryWrapperX<AiLearningExamDO>()
                .eq(AiLearningExamDO::getPhaseId, phaseId)
                .orderByDesc(AiLearningExamDO::getId));
    }

    default List<AiLearningExamDO> selectListByPlanId(Long planId) {
        return selectList(new LambdaQueryWrapperX<AiLearningExamDO>()
                .eq(AiLearningExamDO::getPlanId, planId)
                .orderByDesc(AiLearningExamDO::getId));
    }

}
