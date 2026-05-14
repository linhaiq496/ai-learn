package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AiLearningTaskTypeEnum implements ArrayValuable<Integer> {

    LEARN(10, "学习"),
    REVIEW(20, "复习"),
    EXAM(30, "考试"),
    SUMMARY(40, "总结"),
    MEMORY(50, "长期记忆");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningTaskTypeEnum::getType)
            .toArray(Integer[]::new);

    private final Integer type;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
