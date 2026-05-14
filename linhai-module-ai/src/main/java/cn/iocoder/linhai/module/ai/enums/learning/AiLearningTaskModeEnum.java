package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AiLearningTaskModeEnum implements ArrayValuable<Integer> {

    AI_LEARN(10, "AI 学习"),
    SELF_STUDY(20, "自主学习"),
    POMODORO(30, "番茄钟"),
    AI_REVIEW(40, "AI 复习"),
    AI_EXAM(50, "AI 考试");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningTaskModeEnum::getMode)
            .toArray(Integer[]::new);

    private final Integer mode;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
