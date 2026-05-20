package cn.iocoder.linhai.module.ai.dal.mysql.learn;

import cn.iocoder.linhai.framework.common.pojo.PageResult;
import cn.iocoder.linhai.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.linhai.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.plan.AiLearningPlanPageReqVO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningPlanDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI 学习计划 Mapper
 *
 * @author qzt
 */
@Mapper
public interface AiLearningPlanMapper extends BaseMapperX<AiLearningPlanDO> {

    default PageResult<AiLearningPlanDO> selectPage(AiLearningPlanPageReqVO pageReqVO) {
        return selectPage(pageReqVO, new LambdaQueryWrapperX<AiLearningPlanDO>()
                .likeIfPresent(AiLearningPlanDO::getTitle, pageReqVO.getTitle())
                .eqIfPresent(AiLearningPlanDO::getPlanStatus, pageReqVO.getPlanStatus())
                .eqIfPresent(AiLearningPlanDO::getUserId, pageReqVO.getUserId())
                .betweenIfPresent(AiLearningPlanDO::getCreateTime, pageReqVO.getCreateTime())
                .orderByDesc(AiLearningPlanDO::getId));
    }

}
