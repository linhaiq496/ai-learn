package cn.iocoder.linhai.module.ai.dal.dataobject.learn;

import cn.iocoder.linhai.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AI 学习任务 DO
 *
 * @author qzt
 */
@TableName(value = "ai_learning_task", autoResultMap = true)
@KeySequence("ai_learning_task_seq")
@Data
public class AiLearningTaskDO extends BaseDO {

    @TableId
    private Long id;
    private Long planId;
    private Long phaseId;
    private Long userId;
    /**
     * 任务日期
     */
    private LocalDate taskDate;
    /**
     * 计划开始时间
     */
    private LocalDateTime startTime;
    /**
     * 计划结束时间
     */
    private LocalDateTime endTime;
    /**
     * 任务标题
     */
    private String title;
    /**
     * 任务类型
     */
    private Integer taskType;
    /**
     * 任务模式
     */
    private Integer taskMode;
    /**
     * 任务状态
     */
    private Integer taskStatus;
    /**
     * 学习大纲
     */
    private String contentOutline;
    /**
     * 完成率
     */
    private BigDecimal completionRate;
    /**
     * 实际学习时长
     */
    private Integer actualStudyMinutes;
    /**
     * 关联考试编号
     */
    private Long examId;

}
