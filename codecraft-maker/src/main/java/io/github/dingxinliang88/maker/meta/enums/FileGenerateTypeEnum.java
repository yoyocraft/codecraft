package io.github.dingxinliang88.maker.meta.enums;

/**
 * 文件生成类型枚举
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public enum FileGenerateTypeEnum {

    DYNAMIC("动态生成", "dynamic"),
    STATIC("静态生成", "static"),
    ;

    private final String text;
    private final String value;

    FileGenerateTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
