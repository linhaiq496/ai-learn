package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AiLearningBadgeTypeEnum implements ArrayValuable<Integer> {

    LAZY_DOG(10, "懒狗勋章"),
    GRINDER(20, "卷王勋章"),
    NIGHT_OWL(30, "熬夜战神"),
    STREAK_MASTER(40, "连学达人"),
    REVIEW_MASTER(50, "复习达人"),
    EXAM_ACE(60, "超级卷王");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningBadgeTypeEnum::getType)
            .toArray(Integer[]::new);

    private final Integer type;
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
