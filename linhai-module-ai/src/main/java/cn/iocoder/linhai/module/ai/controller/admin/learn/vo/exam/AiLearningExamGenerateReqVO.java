package cn.iocoder.linhai.module.ai.controller.admin.learn.vo.exam;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - AI 学习考试生成 Request VO")
@Data
public class AiLearningExamGenerateReqVO {

    @Schema(description = "学习计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "学习计划编号不能为空")
    private Long planId;

    @Schema(description = "学习阶段编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "学习阶段编号不能为空")
    private Long phaseId;

    @Schema(description = "学习任务编号", example = "10")
    private Long taskId;

    @Schema(description = "考试名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "IOC 阶段测验")
    @NotBlank(message = "考试名称不能为空")
    private String examName;

    @Schema(description = "是否开启历史错题强化考核", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "是否开启历史错题强化考核不能为空")
    private Boolean wrongQuestionMode;

    @Schema(description = "单选题数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    @NotNull(message = "单选题数量不能为空")
    private Integer singleChoiceCount;

    @Schema(description = "多选题数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "多选题数量不能为空")
    private Integer multipleChoiceCount;

    @Schema(description = "判断题数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
    @NotNull(message = "判断题数量不能为空")
    private Integer judgeCount;

    @Schema(description = "问答题数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "问答题数量不能为空")
    private Integer essayCount;

}
