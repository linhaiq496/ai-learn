package cn.iocoder.linhai.module.ai.dal.dataobject.learn;

import cn.iocoder.linhai.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI 学习阶段 DO
 *
 * @author qzt
 */
@TableName(value = "ai_learning_phase", autoResultMap = true)
@KeySequence("ai_learning_phase_seq")
@Data
public class AiLearningPhaseDO extends BaseDO {

    @TableId
    private Long id;
    /**
     * 学习计划编号
     */
    private Long planId;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 阶段序号
     */
    private Integer phaseIndex;
    /**
     * 阶段标题
     */
    private String phaseTitle;
    /**
     * 阶段目标
     */
    private String phaseGoal;
    /**
     * 阶段学习说明
     */
    private String phaseSummary;
    /**
     * 阶段掌握指标 JSON
     */
    private String masteryTargets;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 阶段状态
     */
    private Integer phaseStatus;
    /**
     * 实际开始时间
     */
    private LocalDateTime startTime;
    /**
     * 实际结束时间
     */
    private LocalDateTime endTime;

}
