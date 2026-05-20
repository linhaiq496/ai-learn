package cn.iocoder.linhai.module.ai.controller.admin.learn.vo.exam;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - AI 学习考试提交 Request VO")
@Data
public class AiLearningExamSubmitReqVO {

    @Schema(description = "考试编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "考试编号不能为空")
    private Long examId;

    @Schema(description = "总分", example = "86.50")
    private BigDecimal score;

    @Schema(description = "客观题得分", example = "42.00")
    private BigDecimal objectiveScore;

    @Schema(description = "主观题得分", example = "44.50")
    private BigDecimal subjectiveScore;

    @Schema(description = "平均正确率", example = "78.00")
    private BigDecimal averageCorrectRate;

    @Schema(description = "答题时长（分钟）", example = "35")
    private Integer durationMinutes;

}
