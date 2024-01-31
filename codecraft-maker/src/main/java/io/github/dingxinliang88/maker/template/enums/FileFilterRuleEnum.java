package io.github.dingxinliang88.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 文件过滤规则枚举
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Getter
public enum FileFilterRuleEnum {
    CONTAINS("包含", "contains"),
    START_WITH("前缀匹配", "startsWith"),
    END_WITH("后缀匹配", "endsWith"),
    REGEX("正则", "regex"),
    EQUALS("等于", "equals"),
    ;

    private final String text;
    private final String value;

    FileFilterRuleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据给定的字符串值获取枚举对象。
     *
     * @param value 字符串值
     * @return 对应的枚举对象，如果不存在则返回 null
     */
    public static FileFilterRuleEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }

        for (FileFilterRuleEnum rangeEnum : FileFilterRuleEnum.values()) {
            if (rangeEnum.getValue().equals(value)) {
                return rangeEnum;
            }
        }
        return null;
    }


}
