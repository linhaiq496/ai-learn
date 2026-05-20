package cn.iocoder.linhai.module.ai.controller.admin.learn.vo.phase;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 学习阶段 Response VO")
@Data
public class AiLearningPhaseRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;
    @Schema(description = "学习计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long planId;
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;
    @Schema(description = "阶段序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer phaseIndex;
    @Schema(description = "阶段标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "基础语法")
    private String phaseTitle;
    @Schema(description = "阶段目标", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phaseGoal;
    @Schema(description = "阶段学习说明")
    private String phaseSummary;
    @Schema(description = "掌握指标 JSON")
    private String masteryTargets;
    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer sort;
    @Schema(description = "阶段状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer phaseStatus;
    @Schema(description = "实际开始时间")
    private LocalDateTime startTime;
    @Schema(description = "实际结束时间")
    private LocalDateTime endTime;
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
