package cn.iocoder.linhai.module.ai.controller.admin.learn.vo.exam;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 学习考试题目 Response VO")
@Data
public class AiLearningExamQuestionRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;
    @Schema(description = "考试编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long examId;
    @Schema(description = "学习计划编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long planId;
    @Schema(description = "学习阶段编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Long phaseId;
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;
    @Schema(description = "题目序号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer questionNo;
    @Schema(description = "题目类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer questionType;
    @Schema(description = "题干内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String stem;
    @Schema(description = "选项 JSON")
    private String optionsJson;
    @Schema(description = "标准答案")
    private String correctAnswer;
    @Schema(description = "答案解析")
    private String answerAnalysis;
    @Schema(description = "知识点 JSON")
    private String knowledgePoints;
    @Schema(description = "用户作答内容")
    private String userAnswer;
    @Schema(description = "是否答对", example = "true")
    private Boolean isCorrect;
    @Schema(description = "题目分值", requiredMode = Schema.RequiredMode.REQUIRED, example = "10.00")
    private BigDecimal score;
    @Schema(description = "实际得分", requiredMode = Schema.RequiredMode.REQUIRED, example = "8.00")
    private BigDecimal earnedScore;
    @Schema(description = "是否错题", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    private Boolean wrongTag;
    @Schema(description = "是否需要复习", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean needsReview;
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
