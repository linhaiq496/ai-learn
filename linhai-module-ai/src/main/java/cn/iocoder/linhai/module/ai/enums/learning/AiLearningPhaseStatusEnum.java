package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 学习阶段状态枚举。
 *
 * 用于描述学习计划下某个阶段的推进状态。
 *
 * @author linhai
 */
@AllArgsConstructor
@Getter
public enum AiLearningPhaseStatusEnum implements ArrayValuable<Integer> {

    /**
     * 待开始。
     *
     * 表示阶段已经生成，但尚未进入执行。
     */
    PENDING(10, "待开始"),
    /**
     * 进行中。
     *
     * 表示当前阶段正在学习或推进中。
     */
    ACTIVE(20, "进行中"),
    /**
     * 已完成。
     *
     * 表示该阶段目标已经达成。
     */
    COMPLETED(30, "已完成"),
    /**
     * 已跳过。
     *
     * 表示该阶段因计划调整等原因不再执行。
     */
    SKIPPED(40, "已跳过");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningPhaseStatusEnum::getStatus)
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
