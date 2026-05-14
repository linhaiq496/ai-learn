package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AiLearningProfileSnapshotTypeEnum implements ArrayValuable<Integer> {

    PROFILE(10, "学习画像"),
    LONG_TERM_MEMORY(20, "长期记忆"),
    DAILY_SUMMARY(30, "每日总结"),
    PLAN_SUMMARY(40, "计划总结");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningProfileSnapshotTypeEnum::getType)
            .toArray(Integer[]::new);

    private final Integer type;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
