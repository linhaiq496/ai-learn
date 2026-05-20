/**
 * @Author：qzt
 * @CreateTime: 2026 -05-18
 **/

package cn.iocoder.linhai.module.ai.service.learn.plan.consumer;


import cn.iocoder.linhai.framework.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import cn.iocoder.linhai.module.ai.dal.dataobject.learn.AiLearningPlanDO;
import cn.iocoder.linhai.module.ai.dal.mysql.learn.AiLearningPlanMapper;
import cn.iocoder.linhai.module.ai.enums.learning.AiLearningPlanStatusEnum;
import cn.iocoder.linhai.module.ai.service.learn.plan.message.LeaningPlanMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static cn.iocoder.linhai.module.ai.enums.AiPlanGenerateConstant.AiLearningPlanGenerateTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiLearningPlanConsumer extends AbstractRedisStreamMessageListener<LeaningPlanMessage> {

    private final AiLearningPlanMapper planMapper;

    @Override
    public void onMessage(LeaningPlanMessage message) {
        log.info("开始消费AI学习计划生成计划消息：{}", message);

        log.info("[onMessage][收到 AI 学习生成计划消息，数据为: {}]", message);

        AiLearningPlanDO aiLearningPlanDO = planMapper.selectById(message.getPlanId());
        if (aiLearningPlanDO == null) {
            log.error("当前生成 《学习计划》任务不存在：{}", message);
            return;
        }

        if (aiLearningPlanDO.getPlanStatus().equals(AiLearningPlanStatusEnum.GENERATING.getStatus())) {
            log.warn("当前 《学习计划》正在生成中，跳过重复处理，planId: {}", message.getPlanId());
            return;
        }
//        提示词准备
        String planTemplate = AiLearningPlanGenerateTemplate
                .replace("{{learningGoal}}", aiLearningPlanDO.getGoal())
                .replace("{{userPrompt}}", aiLearningPlanDO.getGoalPrompt())
                .replace("{{cycleDays}}", String.valueOf(aiLearningPlanDO.getCycleDays()))
                .replace("{{dailyStudyMinutes}}", String.valueOf(aiLearningPlanDO.getDailyStudyMinutes()))
                .replace("{{sourceContent}}", aiLearningPlanDO.getSourceSummary());
//         调用ollama ai接口


    }




}
