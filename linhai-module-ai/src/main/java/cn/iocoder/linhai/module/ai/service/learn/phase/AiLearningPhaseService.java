package cn.iocoder.linhai.module.ai.service.learn.phase;

import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningMaterialDO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningPhaseDO;

import java.util.List;

/**
 * AI 学习阶段 Service 接口
 *
 * @author qzt
 */
public interface AiLearningPhaseService {

    AiLearningPhaseDO getPhase(Long id);

    List<AiLearningPhaseDO> getPhaseListByPlanId(Long planId);

    List<AiLearningMaterialDO> getMaterialListByPhaseId(Long phaseId);

}
