package cn.iocoder.linhai.module.ai.enums.learning;

import cn.iocoder.linhai.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 学习资料类型枚举。
 *
 * 用于区分用户上传或输入的学习资料来源和格式。
 *
 * @author linhai
 */
@AllArgsConstructor
@Getter
public enum AiLearningMaterialTypeEnum implements ArrayValuable<Integer> {

    /**
     * PDF 文档。
     *
     * 表示用户上传的 PDF 格式学习资料。
     */
    PDF(10, "PDF"),
    /**
     * Word 文档。
     *
     * 表示用户上传的 Word 格式学习资料。
     */
    WORD(20, "Word"),
    /**
     * TXT 文本。
     *
     * 表示用户上传的纯文本资料。
     */
    TXT(30, "TXT"),
    /**
     * Markdown 文档。
     *
     * 表示用户上传的 Markdown 格式资料。
     */
    MARKDOWN(40, "Markdown"),
    /**
     * 富文本内容。
     *
     * 表示用户直接输入的带格式文本内容。
     */
    RICH_TEXT(50, "富文本"),
    /**
     * 外部链接。
     *
     * 表示通过 URL 引入的在线学习资料。
     */
    URL(60, "链接");

    public static final Integer[] ARRAYS = Arrays.stream(values())
            .map(AiLearningMaterialTypeEnum::getType)
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
