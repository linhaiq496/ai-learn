package cn.iocoder.linhai.module.ai.dal.mysql.learn;

import cn.iocoder.linhai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.linhai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningPhaseDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 学习阶段 Mapper
 *
 * @author qzt
 */
@Mapper
public interface AiLearningPhaseMapper extends BaseMapperX<AiLearningPhaseDO> {

    default List<AiLearningPhaseDO> selectListByPlanId(Long planId) {
        return selectList(new LambdaQueryWrapperX<AiLearningPhaseDO>()
                .eq(AiLearningPhaseDO::getPlanId, planId)
                .orderByAsc(AiLearningPhaseDO::getSort, AiLearningPhaseDO::getPhaseIndex));
    }

}
