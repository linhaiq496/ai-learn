package cn.iocoder.linhai.module.ai.dal.dataobject.learn;

import cn.iocoder.linhai.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI 学习复习项 DO
 *
 * @author qzt
 */
@TableName(value = "ai_learning_review_item", autoResultMap = true)
@KeySequence("ai_learning_review_item_seq")
@Data
public class AiLearningReviewItemDO extends BaseDO {

    @TableId
    private Long id;
    private Long userId;
    private Long planId;
    private Long phaseId;
    private Long taskId;
    private Long examId;
    private Long questionId;
    private Integer sourceType;
    private String contentTitle;
    private String reviewContent;
    private Integer reviewStatus;
    private Integer reviewCount;
    private BigDecimal easeFactor;
    private Integer intervalDays;
    private LocalDateTime nextReviewTime;
    private LocalDateTime lastReviewTime;

}
