package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AiLearningQuestionTypeEnum implements ArrayValuable<Integer> {

    SINGLE_CHOICE(10, "单选题"),
    MULTIPLE_CHOICE(20, "多选题"),
    JUDGE(30, "判断题"),
    ESSAY(40, "问答题");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningQuestionTypeEnum::getType)
            .toArray(Integer[]::new);

    private final Integer type;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
