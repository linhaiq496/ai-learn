package cn.iocoder.linhai.module.ai.service.learn.plan;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.linhai.framework.common.pojo.PageResult;
import cn.iocoder.linhai.framework.common.util.object.BeanUtils;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.plan.AiLearningPlanPageReqVO;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.plan.AiLearningPlanSaveReqVO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningPlanDO;
import cn.iocoder.linhai.module.ai.dal.mysql.learn.AiLearningPlanMapper;
import cn.iocoder.linhai.module.ai.enums.learning.AiLearningPlanStatusEnum;
import cn.iocoder.linhai.module.ai.service.learn.plan.producer.AiLearningPlanProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * AI 学习计划 Service 实现类
 *
 * @author qzt
 */
@Service
@RequiredArgsConstructor
public class AiLearningPlanServiceImpl implements AiLearningPlanService {


    private final AiLearningPlanMapper planMapper;

    private final AiLearningPlanProducer aiLearningPlanProducer;


    @Override
    public Long createPlan(Long userId, AiLearningPlanSaveReqVO createReqVO) {
        AiLearningPlanDO plan = BeanUtils.toBean(createReqVO, AiLearningPlanDO.class, bean -> {
            bean.setUserId(userId);
            bean.setTotalStudyMinutes(bean.getCycleDays() * bean.getDailyStudyMinutes());
            bean.setPlanStatus(AiLearningPlanStatusEnum.DRAFT.getStatus());
            bean.setTotalPhaseCount(0);
            bean.setOverallMastery(BigDecimal.ZERO);
        });
        planMapper.insert(plan);

        aiLearningPlanProducer.sendLearningPlanMessage(IdUtil.getSnowflake(1, 1).nextId(), plan.getId(), plan.getTitle());

        return plan.getId();
    }

    @Override
    public void updatePlan(Long userId, AiLearningPlanSaveReqVO updateReqVO) {
        AiLearningPlanDO oldPlan = getRequiredPlan(updateReqVO.getId());
        AiLearningPlanDO updateObj = BeanUtils.toBean(updateReqVO, AiLearningPlanDO.class, bean -> {
            bean.setUserId(userId);
            bean.setTotalStudyMinutes(bean.getCycleDays() * bean.getDailyStudyMinutes());
            bean.setPlanStatus(oldPlan.getPlanStatus());
            bean.setCurrentPhaseId(oldPlan.getCurrentPhaseId());
            bean.setTotalPhaseCount(oldPlan.getTotalPhaseCount());
            bean.setOverallMastery(oldPlan.getOverallMastery());
            bean.setLatestScore(oldPlan.getLatestScore());
            bean.setAverageScore(oldPlan.getAverageScore());
            bean.setHighestScore(oldPlan.getHighestScore());
            bean.setLastStudiedTime(oldPlan.getLastStudiedTime());
            bean.setNextReviewTime(oldPlan.getNextReviewTime());
        });
        planMapper.updateById(updateObj);
    }

    @Override
    public void deletePlan(Long id) {
        getRequiredPlan(id);
        planMapper.deleteById(id);
    }

    @Override
    public AiLearningPlanDO getPlan(Long id) {
        return planMapper.selectById(id);
    }

    @Override
    public PageResult<AiLearningPlanDO> getPlanPage(AiLearningPlanPageReqVO pageReqVO) {
        return planMapper.selectPage(pageReqVO);
    }

    @Override
    public void generatePlan(Long id) {
        AiLearningPlanDO plan = getRequiredPlan(id);
        planMapper.updateById(plan.setPlanStatus(AiLearningPlanStatusEnum.GENERATING.getStatus()));
    }

    private AiLearningPlanDO getRequiredPlan(Long id) {
        AiLearningPlanDO plan = planMapper.selectById(id);
        if (plan == null) {
            throw new IllegalArgumentException("学习计划不存在");
        }
        return plan;
    }

}
