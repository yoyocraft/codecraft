package io.github.dingxinliang88.maker.template.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 文件过滤范围枚举
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Getter
public enum FileFilterRangeEnum {
    FILE_NAME("文件名", "fileName"),
    FILE_CONTENT("文件内容", "fileContent"),
    ;

    private final String text;
    private final String value;

    FileFilterRangeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据给定的字符串值获取枚举对象。
     *
     * @param value 字符串值
     * @return 对应的枚举对象，如果不存在则返回 null
     */
    public static FileFilterRangeEnum resolve(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }

        for (FileFilterRangeEnum rangeEnum : FileFilterRangeEnum.values()) {
            if (rangeEnum.value.equals(value)) {
                return rangeEnum;
            }
        }
        return null;
    }


}
