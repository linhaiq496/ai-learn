package cn.iocoder.linhai.module.ai.dal.mysql.learn;

import cn.iocoder.linhai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.linhai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningBadgeRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 学习勋章记录 Mapper
 *
 * @author qzt
 */
@Mapper
public interface AiLearningBadgeRecordMapper extends BaseMapperX<AiLearningBadgeRecordDO> {

    default List<AiLearningBadgeRecordDO> selectListByUserIdAndPlanId(Long userId, Long planId) {
        return selectList(new LambdaQueryWrapperX<AiLearningBadgeRecordDO>()
                .eqIfPresent(AiLearningBadgeRecordDO::getUserId, userId)
                .eqIfPresent(AiLearningBadgeRecordDO::getPlanId, planId)
                .orderByDesc(AiLearningBadgeRecordDO::getUnlockedTime, AiLearningBadgeRecordDO::getId));
    }

}
