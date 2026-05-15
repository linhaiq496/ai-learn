package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 学习勋章状态枚举。
 *
 * 用于表示用户在某个勋章上的当前解锁进度。
 *
 * @author linhai
 */
@AllArgsConstructor
@Getter
public enum AiLearningBadgeStatusEnum implements ArrayValuable<Integer> {

    /**
     * 未解锁。
     *
     * 表示尚未达到该勋章的解锁条件。
     */
    LOCKED(10, "未解锁"),
    /**
     * 冲刺中。
     *
     * 表示已接近解锁条件，仍处于累计进度过程中。
     */
    IN_PROGRESS(20, "冲刺中"),
    /**
     * 已解锁。
     *
     * 表示用户已经满足条件并获得该勋章。
     */
    UNLOCKED(30, "已解锁");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningBadgeStatusEnum::getStatus)
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
