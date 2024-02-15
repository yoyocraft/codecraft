package io.github.dingxinliang88.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 代码片段校验类型枚举
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Getter
public enum CodeSnippetCheckTypeEnum {

    EQUALS("相等", "equals"),
    REGEX("正则", "regex"),
    ;

    private final String text;
    private final String value;

    CodeSnippetCheckTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据给定的字符串值获取枚举对象。
     *
     * @param value 字符串值
     * @return 对应的枚举对象，如果不存在则返回 null
     */
    public static CodeSnippetCheckTypeEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }

        for (CodeSnippetCheckTypeEnum typeEnum : CodeSnippetCheckTypeEnum.values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }

}
