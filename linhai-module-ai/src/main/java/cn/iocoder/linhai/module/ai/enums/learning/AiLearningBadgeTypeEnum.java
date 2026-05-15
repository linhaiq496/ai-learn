package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 学习勋章类型枚举。
 *
 * 用于表示系统内可授予用户的不同成就勋章类型。
 *
 * @author linhai
 */
@AllArgsConstructor
@Getter
public enum AiLearningBadgeTypeEnum implements ArrayValuable<Integer> {

    /**
     * 懒狗勋章。
     *
     * 用于调侃式记录学习不积极或被 AI 督促的场景。
     */
    LAZY_DOG(10, "懒狗勋章"),
    /**
     * 卷王勋章。
     *
     * 表示学习投入高、任务完成效率强的成就勋章。
     */
    GRINDER(20, "卷王勋章"),
    /**
     * 熬夜战神。
     *
     * 表示在夜间持续学习并保持高活跃度的勋章。
     */
    NIGHT_OWL(30, "熬夜战神"),
    /**
     * 连学达人。
     *
     * 表示连续多天保持学习打卡的勋章。
     */
    STREAK_MASTER(40, "连学达人"),
    /**
     * 复习达人。
     *
     * 表示在复习完成度或复习频次方面表现突出的勋章。
     */
    REVIEW_MASTER(50, "复习达人"),
    /**
     * 超级卷王。
     *
     * 表示考试成绩、任务强度或综合学习表现均非常突出的高阶勋章。
     */
    EXAM_ACE(60, "超级卷王");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningBadgeTypeEnum::getType)
            .toArray(Integer[]::new);

    /**
     * 类型编码。
     */
    private final Integer type;
    /**
     * 类型名称。
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
