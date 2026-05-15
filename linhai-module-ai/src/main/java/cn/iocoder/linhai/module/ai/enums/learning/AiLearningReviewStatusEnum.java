package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 复习项状态枚举。
 *
 * 用于表示某个复习项目在复习流程中的当前状态。
 *
 * @author linhai
 */
@AllArgsConstructor
@Getter
public enum AiLearningReviewStatusEnum implements ArrayValuable<Integer> {

    /**
     * 待复习。
     *
     * 表示该知识点已进入复习池，但尚未开始复习。
     */
    PENDING(10, "待复习"),
    /**
     * 复习中。
     *
     * 表示用户或 AI 正在进行该项复习。
     */
    REVIEWING(20, "复习中"),
    /**
     * 已掌握。
     *
     * 表示该知识点经过复习后已达到掌握标准。
     */
    MASTERED(30, "已掌握"),
    /**
     * 已忽略。
     *
     * 表示该复习项被人为忽略或不再继续跟进。
     */
    IGNORED(40, "已忽略");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningReviewStatusEnum::getStatus)
            .toArray(Integer[]::new);

    /**
     * 状态编码。
     */
    private final Integer status;
    /**
     * 状态名称。
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
