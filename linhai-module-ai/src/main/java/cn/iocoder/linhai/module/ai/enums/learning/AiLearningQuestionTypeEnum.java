package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 题目类型枚举。
 *
 * 用于描述 AI 考试中不同题型的分类。
 *
 * @author linhai
 */
@AllArgsConstructor
@Getter
public enum AiLearningQuestionTypeEnum implements ArrayValuable<Integer> {

    /**
     * 单选题。
     *
     * 表示只有一个正确答案的客观题。
     */
    SINGLE_CHOICE(10, "单选题"),
    /**
     * 多选题。
     *
     * 表示存在多个正确答案的客观题。
     */
    MULTIPLE_CHOICE(20, "多选题"),
    /**
     * 判断题。
     *
     * 表示根据题干判断对错的题目。
     */
    JUDGE(30, "判断题"),
    /**
     * 问答题。
     *
     * 表示需要用户自由组织语言作答的主观题。
     */
    ESSAY(40, "问答题");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningQuestionTypeEnum::getType)
            .toArray(Integer[]::new);

    /**
     * 题型编码。
     */
    private final Integer type;
    /**
     * 题型名称。
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
