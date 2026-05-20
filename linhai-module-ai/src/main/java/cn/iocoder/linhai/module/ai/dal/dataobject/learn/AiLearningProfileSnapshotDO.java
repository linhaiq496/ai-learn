package cn.iocoder.linhai.module.ai.dal.dataobject.learn;

import cn.iocoder.linhai.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * AI 学习画像快照 DO
 *
 * @author qzt
 */
@TableName(value = "ai_learning_profile_snapshot", autoResultMap = true)
@KeySequence("ai_learning_profile_snapshot_seq")
@Data
public class AiLearningProfileSnapshotDO extends BaseDO {

    @TableId
    private Long id;
    private Long userId;
    private Long planId;
    private Integer snapshotType;
    private LocalDate snapshotDate;
    private String strengths;
    private String weaknesses;
    private String habits;
    private String memoryPoints;
    private String aiSummary;
    private String metricsJson;

}
