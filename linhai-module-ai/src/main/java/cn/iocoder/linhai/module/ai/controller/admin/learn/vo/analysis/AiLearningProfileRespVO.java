package cn.iocoder.linhai.module.ai.controller.admin.learn.vo.analysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 学习画像快照 Response VO")
@Data
public class AiLearningProfileRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;
    @Schema(description = "学习计划编号", example = "1")
    private Long planId;
    @Schema(description = "快照类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer snapshotType;
    @Schema(description = "快照日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate snapshotDate;
    @Schema(description = "优势总结")
    private String strengths;
    @Schema(description = "薄弱点总结")
    private String weaknesses;
    @Schema(description = "学习习惯总结")
    private String habits;
    @Schema(description = "长期记忆要点")
    private String memoryPoints;
    @Schema(description = "AI 生成总结")
    private String aiSummary;
    @Schema(description = "分析指标 JSON")
    private String metricsJson;
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
