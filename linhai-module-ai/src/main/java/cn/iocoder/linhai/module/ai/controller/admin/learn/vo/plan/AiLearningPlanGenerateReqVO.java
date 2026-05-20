package cn.iocoder.linhai.module.ai.controller.admin.learn.vo.plan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - AI 学习计划生成规划 Request VO")
@Data
public class AiLearningPlanGenerateReqVO {

    @Schema(description = "计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "计划编号不能为空")
    private Long id;

}
