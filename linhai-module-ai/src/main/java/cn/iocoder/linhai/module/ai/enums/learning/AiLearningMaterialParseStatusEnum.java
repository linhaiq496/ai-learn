package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 学习资料解析状态枚举。
 *
 * 用于表示资料上传后被系统解析、切片和提取内容的进度状态。
 *
 * @author linhai
 */
@AllArgsConstructor
@Getter
public enum AiLearningMaterialParseStatusEnum implements ArrayValuable<Integer> {

    /**
     * 待解析。
     *
     * 表示资料已上传，但尚未开始解析。
     */
    PENDING(0, "待解析"),
    /**
     * 解析中。
     *
     * 表示系统正在抽取文本、结构或知识点。
     */
    PARSING(10, "解析中"),
    /**
     * 解析成功。
     *
     * 表示资料已成功转化为可供 AI 使用的内容。
     */
    SUCCESS(20, "解析成功"),
    /**
     * 解析失败。
     *
     * 表示资料处理过程中出现异常，需要重试或更换文件。
     */
    FAILED(30, "解析失败");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningMaterialParseStatusEnum::getStatus)
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
