package cn.iocoder.linhai.module.ai.service.learn.analysis;

import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.analysis.AiLearningDashboardRespVO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningBadgeRecordDO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningProfileSnapshotDO;

import java.util.List;

/**
 * AI 学习分析 Service 接口
 *
 * @author qzt
 */
public interface AiLearningAnalysisService {

    AiLearningDashboardRespVO getDashboard(Long planId);

    List<AiLearningProfileSnapshotDO> getProfileSnapshotList(Long planId);

    List<AiLearningBadgeRecordDO> getBadgeRecordList(Long userId, Long planId);

}
