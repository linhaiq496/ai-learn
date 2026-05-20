package cn.iocoder.linhai.module.ai.controller.admin.learn.vo.plan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 学习计划 Response VO")
@Data
public class AiLearningPlanRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "计划标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "Spring Boot 入门计划")
    private String title;

    @Schema(description = "学习目标", requiredMode = Schema.RequiredMode.REQUIRED)
    private String goal;

    @Schema(description = "补充规划提示词")
    private String goalPrompt;

    @Schema(description = "AI 生成的资料总结")
    private String sourceSummary;

    @Schema(description = "学习周期天数", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    private Integer cycleDays;

    @Schema(description = "每日学习时长（分钟）", requiredMode = Schema.RequiredMode.REQUIRED, example = "120")
    private Integer dailyStudyMinutes;

    @Schema(description = "总学习时长（分钟）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3600")
    private Integer totalStudyMinutes;

    @Schema(description = "计划状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer planStatus;

    @Schema(description = "当前阶段编号", example = "3")
    private Long currentPhaseId;

    @Schema(description = "总阶段数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
    private Integer totalPhaseCount;

    @Schema(description = "整体掌握度", requiredMode = Schema.RequiredMode.REQUIRED, example = "76.50")
    private BigDecimal overallMastery;

    @Schema(description = "最近一次考试得分", example = "88.00")
    private BigDecimal latestScore;

    @Schema(description = "考试平均分", example = "82.50")
    private BigDecimal averageScore;

    @Schema(description = "历史最高分", example = "95.00")
    private BigDecimal highestScore;

    @Schema(description = "最近学习时间")
    private LocalDateTime lastStudiedTime;

    @Schema(description = "下次复习时间")
    private LocalDateTime nextReviewTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
