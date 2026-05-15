package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 学习任务模式枚举。
 *
 * 用于表示任务执行时采用的具体学习方式。
 *
 * @author linhai
 */
@AllArgsConstructor
@Getter
public enum AiLearningTaskModeEnum implements ArrayValuable<Integer> {

    /**
     * AI 学习。
     *
     * 表示由 AI 生成讲解资料、知识卡片或学习内容进行辅助学习。
     */
    AI_LEARN(10, "AI 学习"),
    /**
     * 自主学习。
     *
     * 表示用户按照资料自主阅读和吸收知识。
     */
    SELF_STUDY(20, "自主学习"),
    /**
     * 番茄钟学习。
     *
     * 表示通过专注计时方式进行沉浸式学习。
     */
    POMODORO(30, "番茄钟"),
    /**
     * AI 复习。
     *
     * 表示由 AI 根据遗忘曲线或薄弱点生成复习内容。
     */
    AI_REVIEW(40, "AI 复习"),
    /**
     * AI 考试。
     *
     * 表示由 AI 动态生成试题进行测验。
     */
    AI_EXAM(50, "AI 考试");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningTaskModeEnum::getMode)
            .toArray(Integer[]::new);

    /**
     * 模式编码。
     */
    private final Integer mode;
    /**
     * 模式名称。
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
