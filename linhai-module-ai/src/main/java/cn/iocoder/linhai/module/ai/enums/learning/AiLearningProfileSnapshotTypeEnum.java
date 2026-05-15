package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 学习画像快照类型枚举。
 *
 * 用于区分系统沉淀下来的各类学习分析和记忆快照。
 *
 * @author linhai
 */
@AllArgsConstructor
@Getter
public enum AiLearningProfileSnapshotTypeEnum implements ArrayValuable<Integer> {

    /**
     * 学习画像。
     *
     * 表示用户整体学习习惯、擅长点和薄弱点的画像快照。
     */
    PROFILE(10, "学习画像"),
    /**
     * 长期记忆。
     *
     * 表示系统长期保留的学习偏好和关键知识记忆信息。
     */
    LONG_TERM_MEMORY(20, "长期记忆"),
    /**
     * 每日总结。
     *
     * 表示按天生成的学习总结快照。
     */
    DAILY_SUMMARY(30, "每日总结"),
    /**
     * 计划总结。
     *
     * 表示围绕整个学习计划生成的阶段性或整体总结。
     */
    PLAN_SUMMARY(40, "计划总结");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningProfileSnapshotTypeEnum::getType)
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
