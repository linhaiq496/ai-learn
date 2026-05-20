package cn.iocoder.linhai.module.ai.dal.dataobject.learn;

import cn.iocoder.linhai.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * AI 学习资料 DO
 *
 * @author qzt
 */
@TableName(value = "ai_learning_material", autoResultMap = true)
@KeySequence("ai_learning_material_seq")
@Data
public class AiLearningMaterialDO extends BaseDO {

    @TableId
    private Long id;
    /**
     * 学习阶段编号
     */
    private Long phaseId;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 关联知识库编号
     */
    private Long knowledgeId;
    /**
     * 关联知识库文档编号
     */
    private Long knowledgeDocumentId;
    /**
     * 关联文件编号
     */
    private Long fileId;
    /**
     * 资料类型
     */
    private Integer materialType;
    /**
     * 资料名称
     */
    private String name;
    /**
     * 存储路径
     */
    private String storagePath;
    /**
     * 内容哈希
     */
    private String contentHash;
    /**
     * 原始内容
     */
    private String originalContent;
    /**
     * 解析后的内容
     */
    private String parsedContent;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 解析状态
     */
    private Integer parseStatus;
    /**
     * 错误信息
     */
    private String errorMessage;

}
