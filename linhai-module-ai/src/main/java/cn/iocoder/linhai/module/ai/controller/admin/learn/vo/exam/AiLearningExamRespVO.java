package cn.iocoder.linhai.module.ai.controller.admin.learn.vo.exam;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 学习考试 Response VO")
@Data
public class AiLearningExamRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;
    @Schema(description = "学习计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long planId;
    @Schema(description = "学习阶段编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Long phaseId;
    @Schema(description = "学习任务编号", example = "10")
    private Long taskId;
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;
    @Schema(description = "考试名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "IOC 阶段测验")
    private String examName;
    @Schema(description = "是否开启历史错题强化考核", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean wrongQuestionMode;
    @Schema(description = "单选题数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Integer singleChoiceCount;
    @Schema(description = "多选题数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private Integer multipleChoiceCount;
    @Schema(description = "判断题数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
    private Integer judgeCount;
    @Schema(description = "问答题数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Integer essayCount;
    @Schema(description = "题目总数", requiredMode = Schema.RequiredMode.REQUIRED, example = "14")
    private Integer totalQuestionCount;
    @Schema(description = "考试状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer examStatus;
    @Schema(description = "总分", example = "86.50")
    private BigDecimal score;
    @Schema(description = "客观题得分", example = "42.00")
    private BigDecimal objectiveScore;
    @Schema(description = "主观题得分", example = "44.50")
    private BigDecimal subjectiveScore;
    @Schema(description = "平均正确率", example = "78.00")
    private BigDecimal averageCorrectRate;
    @Schema(description = "薄弱知识点 JSON")
    private String weakPoints;
    @Schema(description = "开始答题时间")
    private LocalDateTime startedTime;
    @Schema(description = "提交答卷时间")
    private LocalDateTime submittedTime;
    @Schema(description = "评分完成时间")
    private LocalDateTime gradedTime;
    @Schema(description = "考试时长（分钟）", requiredMode = Schema.RequiredMode.REQUIRED, example = "35")
    private Integer durationMinutes;
    @Schema(description = "异常错误信息")
    private String errorMessage;
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
