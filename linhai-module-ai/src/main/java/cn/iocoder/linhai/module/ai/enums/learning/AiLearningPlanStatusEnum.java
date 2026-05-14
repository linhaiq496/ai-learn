package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AiLearningPlanStatusEnum implements ArrayValuable<Integer> {

    DRAFT(0, "草稿"),
    GENERATING(10, "生成中"),
    ACTIVE(20, "进行中"),
    PAUSED(30, "已暂停"),
    COMPLETED(40, "已完成"),
    ARCHIVED(50, "已归档");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningPlanStatusEnum::getStatus)
            .toArray(Integer[]::new);

    private final Integer status;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
