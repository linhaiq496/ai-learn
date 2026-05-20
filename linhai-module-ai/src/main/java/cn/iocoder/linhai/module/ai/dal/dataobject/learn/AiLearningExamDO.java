package cn.iocoder.linhai.module.ai.dal.dataobject.learn;

import cn.iocoder.linhai.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI 学习考试 DO
 *
 * @author qzt
 */
@TableName(value = "ai_learning_exam", autoResultMap = true)
@KeySequence("ai_learning_exam_seq")
@Data
public class AiLearningExamDO extends BaseDO {

    @TableId
    private Long id;
    private Long planId;
    private Long phaseId;
    private Long taskId;
    private Long userId;
    /**
     * 考试名称
     */
    private String examName;
    /**
     * 是否开启历史错题强化考核
     */
    private Boolean wrongQuestionMode;
    private Integer singleChoiceCount;
    private Integer multipleChoiceCount;
    private Integer judgeCount;
    private Integer essayCount;
    private Integer totalQuestionCount;
    private Integer examStatus;
    private BigDecimal score;
    private BigDecimal objectiveScore;
    private BigDecimal subjectiveScore;
    private BigDecimal averageCorrectRate;
    /**
     * 薄弱知识点 JSON
     */
    private String weakPoints;
    private LocalDateTime startedTime;
    private LocalDateTime submittedTime;
    private LocalDateTime gradedTime;
    private Integer durationMinutes;
    private String errorMessage;

}
