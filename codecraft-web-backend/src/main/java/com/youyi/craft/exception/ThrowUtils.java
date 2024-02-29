package com.youyi.craft.exception;

import com.youyi.craft.common.ErrorCode;

/**
 * 抛异常工具类
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class ThrowUtils {


    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }


    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
