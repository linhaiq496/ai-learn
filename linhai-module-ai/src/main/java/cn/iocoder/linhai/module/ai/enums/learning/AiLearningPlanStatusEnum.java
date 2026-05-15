package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 学习计划状态枚举。
 *
 * 用于描述一个学习计划从创建、生成到完成归档的完整生命周期。
 *
 * @author linhai
 */
@AllArgsConstructor
@Getter
public enum AiLearningPlanStatusEnum implements ArrayValuable<Integer> {

    /**
     * 草稿。
     *
     * 表示学习目标已经创建，但还没有正式生成学习规划。
     */
    DRAFT(0, "草稿"),
    /**
     * 生成中。
     *
     * 表示 AI 正在分析资料并生成完整学习计划。
     */
    GENERATING(10, "生成中"),
    /**
     * 进行中。
     *
     * 表示学习计划已经启用，用户正在按计划学习。
     */
    ACTIVE(20, "进行中"),
    /**
     * 已暂停。
     *
     * 表示计划暂时中止，但后续仍可以恢复继续执行。
     */
    PAUSED(30, "已暂停"),
    /**
     * 已完成。
     *
     * 表示该学习计划的目标已经完成。
     */
    COMPLETED(40, "已完成"),
    /**
     * 已归档。
     *
     * 表示计划已结束，仅用于历史查看和复盘。
     */
    ARCHIVED(50, "已归档");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningPlanStatusEnum::getStatus)
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
