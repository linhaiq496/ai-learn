package cn.iocoder.linhai.module.ai.controller.admin.learn.vo.plan;

import cn.iocoder.linhai.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.linhai.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - AI 学习计划分页 Request VO")
@Data
public class AiLearningPlanPageReqVO extends PageParam {

    @Schema(description = "计划标题", example = "Java 学习计划")
    private String title;

    @Schema(description = "计划状态", example = "20")
    private Integer planStatus;

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
