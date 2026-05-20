package cn.iocoder.linhai.module.ai.service.learn.exam;

import cn.iocoder.linhai.framework.common.util.object.BeanUtils;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.exam.AiLearningExamGenerateReqVO;
import cn.iocoder.linhai.module.ai.controller.admin.learn.vo.exam.AiLearningExamSubmitReqVO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningExamDO;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningExamQuestionDO;
import cn.iocoder.linhai.module.ai.dal.mysql.learn.AiLearningExamMapper;
import cn.iocoder.linhai.module.ai.dal.mysql.learn.AiLearningExamQuestionMapper;
import cn.iocoder.linhai.module.ai.enums.learning.AiLearningExamStatusEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI 学习考试 Service 实现类
 *
 * @author qzt
 */
@Service
public class AiLearningExamServiceImpl implements AiLearningExamService {

    @Resource
    private AiLearningExamMapper examMapper;
    @Resource
    private AiLearningExamQuestionMapper examQuestionMapper;

    @Override
    public Long createExam(Long userId, AiLearningExamGenerateReqVO createReqVO) {
        AiLearningExamDO exam = BeanUtils.toBean(createReqVO, AiLearningExamDO.class, bean -> {
            bean.setUserId(userId);
            bean.setTotalQuestionCount(bean.getSingleChoiceCount() + bean.getMultipleChoiceCount()
                    + bean.getJudgeCount() + bean.getEssayCount());
            bean.setExamStatus(AiLearningExamStatusEnum.READY.getStatus());
        });
        examMapper.insert(exam);
        return exam.getId();
    }

    @Override
    public AiLearningExamDO getExam(Long id) {
        return examMapper.selectById(id);
    }

    @Override
    public List<AiLearningExamQuestionDO> getQuestionListByExamId(Long examId) {
        return examQuestionMapper.selectListByExamId(examId);
    }

    @Override
    public void submitExam(Long userId, AiLearningExamSubmitReqVO submitReqVO) {
        AiLearningExamDO exam = getRequiredExam(submitReqVO.getExamId());
        exam.setUserId(userId);
        exam.setScore(submitReqVO.getScore());
        exam.setObjectiveScore(submitReqVO.getObjectiveScore());
        exam.setSubjectiveScore(submitReqVO.getSubjectiveScore());
        exam.setAverageCorrectRate(submitReqVO.getAverageCorrectRate());
        exam.setDurationMinutes(submitReqVO.getDurationMinutes());
        exam.setSubmittedTime(LocalDateTime.now());
        exam.setExamStatus(AiLearningExamStatusEnum.FINISHED.getStatus());
        examMapper.updateById(exam);
    }

    private AiLearningExamDO getRequiredExam(Long id) {
        AiLearningExamDO exam = examMapper.selectById(id);
        if (exam == null) {
            throw new IllegalArgumentException("学习考试不存在");
        }
        return exam;
    }

}
