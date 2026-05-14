package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AiLearningMaterialParseStatusEnum implements ArrayValuable<Integer> {

    PENDING(0, "待解析"),
    PARSING(10, "解析中"),
    SUCCESS(20, "解析成功"),
    FAILED(30, "解析失败");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningMaterialParseStatusEnum::getStatus)
            .toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
