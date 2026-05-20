package cn.iocoder.linhai.module.ai.dal.mysql.learn;

import cn.iocoder.linhai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.linhai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningReviewItemDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 学习复习项 Mapper
 *
 * @author qzt
 */
@Mapper
public interface AiLearningReviewItemMapper extends BaseMapperX<AiLearningReviewItemDO> {

    default List<AiLearningReviewItemDO> selectListByPlanId(Long planId) {
        return selectList(new LambdaQueryWrapperX<AiLearningReviewItemDO>()
                .eq(AiLearningReviewItemDO::getPlanId, planId)
                .orderByDesc(AiLearningReviewItemDO::getNextReviewTime, AiLearningReviewItemDO::getId));
    }

}
