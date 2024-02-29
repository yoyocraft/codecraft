package com.youyi.craft.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 用户角色枚举
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Getter
public enum UserRoleEnum {

    USER("用户", "user"),
    ADMIN("管理员", "admin"),
    BAN("被封号", "ban");

    private final String text;

    private final String value;

    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static UserRoleEnum resolve(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

}
