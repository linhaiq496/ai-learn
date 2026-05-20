package cn.iocoder.linhai.module.ai.dal.dataobject.learn;

import cn.iocoder.linhai.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * AI 考试题目 DO
 *
 * @author qzt
 */
@TableName(value = "ai_learning_exam_question", autoResultMap = true)
@KeySequence("ai_learning_exam_question_seq")
@Data
public class AiLearningExamQuestionDO extends BaseDO {

    @TableId
    private Long id;
    private Long examId;
    private Long planId;
    private Long phaseId;
    private Long userId;
    private Integer questionNo;
    private Integer questionType;
    private String stem;
    private String optionsJson;
    private String correctAnswer;
    private String answerAnalysis;
    private String knowledgePoints;
    private String userAnswer;
    private Boolean isCorrect;
    private BigDecimal score;
    private BigDecimal earnedScore;
    private Boolean wrongTag;
    private Boolean needsReview;

}
