package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 学习任务类型枚举。
 *
 * 用于区分计划中不同性质的任务，以便前端展示和后端编排。
 *
 * @author linhai
 */
@AllArgsConstructor
@Getter
public enum AiLearningTaskTypeEnum implements ArrayValuable<Integer> {

    /**
     * 学习任务。
     *
     * 表示常规的新知识学习任务。
     */
    LEARN(10, "学习"),
    /**
     * 复习任务。
     *
     * 表示针对已学内容的回顾和强化任务。
     */
    REVIEW(20, "复习"),
    /**
     * 考试任务。
     *
     * 表示用于检测掌握程度的测验或考试任务。
     */
    EXAM(30, "考试"),
    /**
     * 总结任务。
     *
     * 表示对某日、某阶段或某计划进行归纳总结的任务。
     */
    SUMMARY(40, "总结"),
    /**
     * 长期记忆任务。
     *
     * 表示用于巩固长期记忆和重复提取的专项任务。
     */
    MEMORY(50, "长期记忆");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningTaskTypeEnum::getType)
            .toArray(Integer[]::new);

    /**
     * 类型编码。
     */
    private final Integer type;
    /**
     * 类型名称。
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
