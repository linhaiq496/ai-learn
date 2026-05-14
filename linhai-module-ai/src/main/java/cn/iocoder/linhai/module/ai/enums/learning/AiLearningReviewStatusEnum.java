package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AiLearningReviewStatusEnum implements ArrayValuable<Integer> {

    PENDING(10, "待复习"),
    REVIEWING(20, "复习中"),
    MASTERED(30, "已掌握"),
    IGNORED(40, "已忽略");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningReviewStatusEnum::getStatus)
            .toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
