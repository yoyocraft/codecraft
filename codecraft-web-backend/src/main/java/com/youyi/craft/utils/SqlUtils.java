package com.youyi.craft.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * SQL 工具
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class SqlUtils {

    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}
