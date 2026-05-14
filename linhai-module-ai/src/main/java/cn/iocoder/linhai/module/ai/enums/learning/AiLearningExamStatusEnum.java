package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AiLearningExamStatusEnum implements ArrayValuable<Integer> {

    GENERATING(10, "生成中"),
    READY(20, "待作答"),
    ANSWERING(30, "作答中"),
    GRADING(40, "评分中"),
    FINISHED(50, "已完成"),
    FAILED(60, "已失败"),
    CANCELLED(70, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningExamStatusEnum::getStatus)
            .toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
