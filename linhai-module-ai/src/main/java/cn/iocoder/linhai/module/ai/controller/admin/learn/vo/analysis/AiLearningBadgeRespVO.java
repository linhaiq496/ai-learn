package cn.iocoder.linhai.module.ai.controller.admin.learn.vo.analysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 学习勋章记录 Response VO")
@Data
public class AiLearningBadgeRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;
    @Schema(description = "学习计划编号", example = "1")
    private Long planId;
    @Schema(description = "勋章类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer badgeType;
    @Schema(description = "勋章状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    private Integer badgeStatus;
    @Schema(description = "当前进度值", requiredMode = Schema.RequiredMode.REQUIRED, example = "7.00")
    private BigDecimal progressValue;
    @Schema(description = "目标进度值", requiredMode = Schema.RequiredMode.REQUIRED, example = "10.00")
    private BigDecimal progressTarget;
    @Schema(description = "解锁时间")
    private LocalDateTime unlockedTime;
    @Schema(description = "勋章备注")
    private String remark;
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
