package cn.iocoder.linhai.module.ai.dal.dataobject.learn;

import cn.iocoder.linhai.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI 学习计划 DO
 *
 * @author qzt
 */
@TableName(value = "ai_learning_plan", autoResultMap = true)
@KeySequence("ai_learning_plan_seq")
@Data
public class AiLearningPlanDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 计划标题
     */
    private String title;
    /**
     * 学习目标
     */
    private String goal;
    /**
     * 补充规划提示词
     */
    private String goalPrompt;
    /**
     * AI 生成的资料总结
     */
    private String sourceSummary;
    /**
     * 学习周期天数
     */
    private Integer cycleDays;
    /**
     * 每日学习时长（分钟）
     */
    private Integer dailyStudyMinutes;
    /**
     * 计划总学习时长（分钟）
     */
    private Integer totalStudyMinutes;
    /**
     * 计划状态
     */
    private Integer planStatus;
    /**
     * 当前阶段编号
     */
    private Long currentPhaseId;
    /**
     * 总阶段数量
     */
    private Integer totalPhaseCount;
    /**
     * 整体掌握度
     */
    private BigDecimal overallMastery;
    /**
     * 最近一次考试得分
     */
    private BigDecimal latestScore;
    /**
     * 平均分
     */
    private BigDecimal averageScore;
    /**
     * 最高分
     */
    private BigDecimal highestScore;
    /**
     * 最近学习时间
     */
    private LocalDateTime lastStudiedTime;
    /**
     * 下次复习时间
     */
    private LocalDateTime nextReviewTime;

}
