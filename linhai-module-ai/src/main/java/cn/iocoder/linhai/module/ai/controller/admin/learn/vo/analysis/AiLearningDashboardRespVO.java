package cn.iocoder.linhai.module.ai.controller.admin.learn.vo.analysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 学习分析总览 Response VO")
@Data
public class AiLearningDashboardRespVO {

    @Schema(description = "学习计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long planId;

    @Schema(description = "总阶段数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
    private Integer totalPhaseCount;

    @Schema(description = "已完成阶段数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer completedPhaseCount;

    @Schema(description = "考试总数", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private Integer totalExamCount;

    @Schema(description = "平均分", example = "82.50")
    private BigDecimal averageScore;

    @Schema(description = "最高分", example = "95.00")
    private BigDecimal highestScore;

    @Schema(description = "整体掌握度", example = "76.50")
    private BigDecimal overallMastery;

    @Schema(description = "下次复习时间")
    private LocalDateTime nextReviewTime;

}
