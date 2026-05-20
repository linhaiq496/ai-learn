package cn.iocoder.linhai.module.ai.controller.admin.learn.vo.plan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - AI 学习计划新增/修改 Request VO")
@Data
public class AiLearningPlanSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "计划标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "Spring Boot 入门计划")
    @NotBlank(message = "计划标题不能为空")
    private String title;

    @Schema(description = "学习目标", requiredMode = Schema.RequiredMode.REQUIRED, example = "30 天掌握 Spring Boot 常用开发能力")
    @NotBlank(message = "学习目标不能为空")
    private String goal;

    @Schema(description = "补充规划提示词", example = "偏重项目实战和底层原理")
    private String goalPrompt;

    @Schema(description = "学习周期天数", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    @NotNull(message = "学习周期天数不能为空")
    private Integer cycleDays;

    @Schema(description = "每日学习时长（分钟）", requiredMode = Schema.RequiredMode.REQUIRED, example = "120")
    @NotNull(message = "每日学习时长不能为空")
    private Integer dailyStudyMinutes;

}
