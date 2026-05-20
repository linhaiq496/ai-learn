package cn.iocoder.linhai.module.ai.dal.mysql.learn;

import cn.iocoder.linhai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.linhai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningProfileSnapshotDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 学习画像快照 Mapper
 *
 * @author qzt
 */
@Mapper
public interface AiLearningProfileSnapshotMapper extends BaseMapperX<AiLearningProfileSnapshotDO> {

    default List<AiLearningProfileSnapshotDO> selectListByPlanId(Long planId) {
        return selectList(new LambdaQueryWrapperX<AiLearningProfileSnapshotDO>()
                .eqIfPresent(AiLearningProfileSnapshotDO::getPlanId, planId)
                .orderByDesc(AiLearningProfileSnapshotDO::getSnapshotDate, AiLearningProfileSnapshotDO::getId));
    }

}
