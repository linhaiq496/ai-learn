package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AiLearningBadgeStatusEnum implements ArrayValuable<Integer> {

    LOCKED(10, "未解锁"),
    IN_PROGRESS(20, "冲刺中"),
    UNLOCKED(30, "已解锁");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningBadgeStatusEnum::getStatus)
            .toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
