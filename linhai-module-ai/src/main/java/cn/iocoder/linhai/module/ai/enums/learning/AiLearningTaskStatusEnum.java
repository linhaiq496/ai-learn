package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 学习任务状态枚举。
 *
 * 用于表示每日任务、复习任务或考试任务的执行进度。
 *
 * @author linhai
 */
@AllArgsConstructor
@Getter
public enum AiLearningTaskStatusEnum implements ArrayValuable<Integer> {

    /**
     * 待开始。
     *
     * 表示任务已生成，但用户尚未开始执行。
     */
    PENDING(10, "待开始"),
    /**
     * 进行中。
     *
     * 表示任务已经开始，仍在执行过程中。
     */
    IN_PROGRESS(20, "进行中"),
    /**
     * 已完成。
     *
     * 表示任务已按要求完成。
     */
    COMPLETED(30, "已完成"),
    /**
     * 已逾期。
     *
     * 表示任务未在规定时间内完成。
     */
    OVERDUE(40, "已逾期"),
    /**
     * 已跳过。
     *
     * 表示任务被用户或系统主动跳过。
     */
    SKIPPED(50, "已跳过");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningTaskStatusEnum::getStatus)
            .toArray(Integer[]::new);

    /**
     * 状态编码。
     */
    private final Integer status;
    /**
     * 状态名称。
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
