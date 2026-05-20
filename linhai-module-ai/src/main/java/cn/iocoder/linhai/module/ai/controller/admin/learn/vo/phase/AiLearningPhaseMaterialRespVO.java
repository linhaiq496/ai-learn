package cn.iocoder.linhai.module.ai.controller.admin.learn.vo.phase;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 学习阶段资料 Response VO")
@Data
public class AiLearningPhaseMaterialRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;
    @Schema(description = "阶段编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Long phaseId;
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;
    @Schema(description = "知识库编号", example = "100")
    private Long knowledgeId;
    @Schema(description = "知识库文档编号", example = "1000")
    private Long knowledgeDocumentId;
    @Schema(description = "文件编号", example = "2000")
    private Long fileId;
    @Schema(description = "资料类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer materialType;
    @Schema(description = "资料名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "IOC 容器原理")
    private String name;
    @Schema(description = "存储路径")
    private String storagePath;
    @Schema(description = "内容哈希")
    private String contentHash;
    @Schema(description = "原始内容")
    private String originalContent;
    @Schema(description = "解析后的内容")
    private String parsedContent;
    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer sort;
    @Schema(description = "解析状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "20")
    private Integer parseStatus;
    @Schema(description = "解析错误信息")
    private String errorMessage;
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
