package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AiLearningMaterialTypeEnum implements ArrayValuable<Integer> {

    PDF(10, "PDF"),
    WORD(20, "Word"),
    TXT(30, "TXT"),
    MARKDOWN(40, "Markdown"),
    RICH_TEXT(50, "富文本"),
    URL(60, "链接");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningMaterialTypeEnum::getType)
            .toArray(Integer[]::new);

    private final Integer type;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
