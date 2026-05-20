/**
 * @Author：qzt
 * @CreateTime: 2026 -05-18
 **/

package cn.iocoder.linhai.module.ai.service.learn.plan.message;

import cn.iocoder.linhai.framework.mq.redis.core.stream.AbstractRedisStreamMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaningPlanMessage extends AbstractRedisStreamMessage {

    @NotNull(message = "消息id不能为空")
    private Long messageId;

    @NotNull(message = "计划id不能为空")
    private Long planId;

    @NotNull(message = "计划名称不能为空")
    private String planName;
}
