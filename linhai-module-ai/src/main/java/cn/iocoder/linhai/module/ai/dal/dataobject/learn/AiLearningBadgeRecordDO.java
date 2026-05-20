package cn.iocoder.linhai.module.ai.dal.dataobject.learn;

import cn.iocoder.linhai.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI 学习勋章记录 DO
 *
 * @author qzt
 */
@TableName(value = "ai_learning_badge_record", autoResultMap = true)
@KeySequence("ai_learning_badge_record_seq")
@Data
public class AiLearningBadgeRecordDO extends BaseDO {

    @TableId
    private Long id;
    private Long userId;
    private Long planId;
    private Integer badgeType;
    private Integer badgeStatus;
    private BigDecimal progressValue;
    private BigDecimal progressTarget;
    private LocalDateTime unlockedTime;
    private String remark;

}
