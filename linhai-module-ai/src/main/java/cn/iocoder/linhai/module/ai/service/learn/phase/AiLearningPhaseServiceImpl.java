package cn.iocoder.linhai.module.ai.service.learn.phase;

import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningMaterialDO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningPhaseDO;
import cn.iocoder.linhai.module.ai.dal.mysql.learn.AiLearningMaterialMapper;
import cn.iocoder.linhai.module.ai.dal.mysql.learn.AiLearningPhaseMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * AI 学习阶段 Service 实现类
 *
 * @author qzt
 */
@Service
public class AiLearningPhaseServiceImpl implements AiLearningPhaseService {

    @Resource
    private AiLearningPhaseMapper phaseMapper;
    @Resource
    private AiLearningMaterialMapper materialMapper;

    @Override
    public AiLearningPhaseDO getPhase(Long id) {
        return phaseMapper.selectById(id);
    }

    @Override
    public List<AiLearningPhaseDO> getPhaseListByPlanId(Long planId) {
        return phaseMapper.selectListByPlanId(planId);
    }

    @Override
    public List<AiLearningMaterialDO> getMaterialListByPhaseId(Long phaseId) {
        return materialMapper.selectListByPhaseId(phaseId);
    }

}
