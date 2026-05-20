package cn.iocoder.linhai.module.ai.service.learn.plan;

import cn.iocoder.linhai.framework.common.pojo.PageResult;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.plan.AiLearningPlanPageReqVO;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.plan.AiLearningPlanSaveReqVO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningPlanDO;

/**
 * AI 学习计划 Service 接口
 *
 * @author qzt
 */
public interface AiLearningPlanService {

    Long createPlan(Long userId, AiLearningPlanSaveReqVO createReqVO);

    void updatePlan(Long userId, AiLearningPlanSaveReqVO updateReqVO);

    void deletePlan(Long id);

    AiLearningPlanDO getPlan(Long id);

    PageResult<AiLearningPlanDO> getPlanPage(AiLearningPlanPageReqVO pageReqVO);

    void generatePlan(Long id);

}
