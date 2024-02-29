package com.youyi.craft.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 文件上传业务类型枚举
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Getter
public enum FileUploadBizEnum {

    USER_AVATAR("用户头像", "user_avatar"),
    GENERATOR_PICTURE("生成器图片", "generator_picture"),
    GENERATOR_DIST("生成器产物包", "generator_dist"),
    GENERATOR_MAKE_TEMPLATE("生成器模板", "generator_make_template"),
    ;

    private final String text;

    private final String value;

    FileUploadBizEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static FileUploadBizEnum resolve(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (FileUploadBizEnum anEnum : FileUploadBizEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

}
