package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AiLearningPhaseStatusEnum implements ArrayValuable<Integer> {

    PENDING(10, "待开始"),
    ACTIVE(20, "进行中"),
    COMPLETED(30, "已完成"),
    SKIPPED(40, "已跳过");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningPhaseStatusEnum::getStatus)
            .toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
