package cn.iocoder.linhai.module.ai.service.learn.exam;

import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.exam.AiLearningExamGenerateReqVO;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.exam.AiLearningExamSubmitReqVO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningExamDO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningExamQuestionDO;

import java.util.List;

/**
 * AI 学习考试 Service 接口
 *
 * @author qzt
 */
public interface AiLearningExamService {

    Long createExam(Long userId, AiLearningExamGenerateReqVO createReqVO);

    AiLearningExamDO getExam(Long id);

    List<AiLearningExamQuestionDO> getQuestionListByExamId(Long examId);

    void submitExam(Long userId, AiLearningExamSubmitReqVO submitReqVO);

}
