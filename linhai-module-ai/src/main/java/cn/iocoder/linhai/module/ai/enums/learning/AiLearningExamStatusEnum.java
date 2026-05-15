package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 学习考试状态枚举。
 *
 * 用于描述 AI 考试从生成试卷到评分结束的完整流程状态。
 *
 * @author linhai
 */
@AllArgsConstructor
@Getter
public enum AiLearningExamStatusEnum implements ArrayValuable<Integer> {

    /**
     * 生成中。
     *
     * 表示 AI 正在根据知识点和薄弱项生成试卷。
     */
    GENERATING(10, "生成中"),
    /**
     * 待作答。
     *
     * 表示试卷已经生成完成，等待用户开始答题。
     */
    READY(20, "待作答"),
    /**
     * 作答中。
     *
     * 表示用户当前正在完成考试题目。
     */
    ANSWERING(30, "作答中"),
    /**
     * 评分中。
     *
     * 表示系统正在分析答案并进行评分。
     */
    GRADING(40, "评分中"),
    /**
     * 已完成。
     *
     * 表示考试流程结束，并已产生结果。
     */
    FINISHED(50, "已完成"),
    /**
     * 已失败。
     *
     * 表示考试生成或评分过程出现异常失败。
     */
    FAILED(60, "已失败"),
    /**
     * 已取消。
     *
     * 表示本次考试被用户或系统主动取消。
     */
    CANCELLED(70, "已取消");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningExamStatusEnum::getStatus)
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
