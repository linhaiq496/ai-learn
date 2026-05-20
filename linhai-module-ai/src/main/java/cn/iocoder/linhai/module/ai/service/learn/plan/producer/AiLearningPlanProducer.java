/**
 * @Author：qzt
 * @CreateTime: 2026 -05-18
 **/

package cn.iocoder.linhai.module.ai.service.learn.plan.producer;

import cn.iocoder.linhai.framework.mq.redis.core.RedisMQTemplate;
import cn.iocoder.linhai.module.ai.service.learn.plan.message.LeaningPlanMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiLearningPlanProducer {

    private final RedisMQTemplate redisMQTemplate;


    public void sendLearningPlanMessage(Long messageId, Long planId, String planName) {
        LeaningPlanMessage message = new LeaningPlanMessage();
        message.setMessageId(messageId);
        message.setPlanId(planId);
        message.setPlanName(planName);
        redisMQTemplate.send(message);
    }

}
