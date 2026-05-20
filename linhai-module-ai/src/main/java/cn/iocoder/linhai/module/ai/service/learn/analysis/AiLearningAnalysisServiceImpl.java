package cn.iocoder.linhai.module.ai.service.learn.analysis;

import cn.iocoder.linhai.framework.common.util.object.BeanUtils;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.analysis.AiLearningDashboardRespVO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningBadgeRecordDO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningExamDO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningPhaseDO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningPlanDO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningProfileSnapshotDO;
import cn.iocoder.linhai.module.ai.dal.mysql.learn.AiLearningBadgeRecordMapper;
import cn.iocoder.linhai.module.ai.dal.mysql.learn.AiLearningExamMapper;
import cn.iocoder.linhai.module.ai.dal.mysql.learn.AiLearningPhaseMapper;
import cn.iocoder.linhai.module.ai.dal.mysql.learn.AiLearningPlanMapper;
import cn.iocoder.linhai.module.ai.dal.mysql.learn.AiLearningProfileSnapshotMapper;
import cn.iocoder.linhai.module.ai.enums.learning.AiLearningPhaseStatusEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * AI 学习分析 Service 实现类
 *
 * @author qzt
 */
@Service
public class AiLearningAnalysisServiceImpl implements AiLearningAnalysisService {

    @Resource
    private AiLearningPlanMapper planMapper;
    @Resource
    private AiLearningPhaseMapper phaseMapper;
    @Resource
    private AiLearningExamMapper examMapper;
    @Resource
    private AiLearningProfileSnapshotMapper profileSnapshotMapper;
    @Resource
    private AiLearningBadgeRecordMapper badgeRecordMapper;

    @Override
    public AiLearningDashboardRespVO getDashboard(Long planId) {
        AiLearningPlanDO plan = planMapper.selectById(planId);
        if (plan == null) {
            throw new IllegalArgumentException("学习计划不存在");
        }
        List<AiLearningPhaseDO> phases = phaseMapper.selectListByPlanId(planId);
        List<AiLearningExamDO> exams = examMapper.selectListByPlanId(planId);
        long completedPhaseCount = phases.stream()
                .filter(phase -> AiLearningPhaseStatusEnum.COMPLETED.getStatus().equals(phase.getPhaseStatus()))
                .count();
        BigDecimal maxScore = exams.stream()
                .map(AiLearningExamDO::getScore)
                .filter(score -> score != null)
                .max(BigDecimal::compareTo)
                .orElse(plan.getHighestScore());
        BigDecimal avgScore = plan.getAverageScore();
        if (avgScore == null) {
            avgScore = BigDecimal.ZERO;
        }
        return BeanUtils.toBean(plan, AiLearningDashboardRespVO.class, bean -> {
            bean.setPlanId(planId);
            bean.setCompletedPhaseCount((int) completedPhaseCount);
            bean.setTotalExamCount(exams.size());
            bean.setAverageScore(avgScore);
            bean.setHighestScore(maxScore);
        });
    }

    @Override
    public List<AiLearningProfileSnapshotDO> getProfileSnapshotList(Long planId) {
        return profileSnapshotMapper.selectListByPlanId(planId);
    }

    @Override
    public List<AiLearningBadgeRecordDO> getBadgeRecordList(Long userId, Long planId) {
        return badgeRecordMapper.selectListByUserIdAndPlanId(userId, planId);
    }

}
