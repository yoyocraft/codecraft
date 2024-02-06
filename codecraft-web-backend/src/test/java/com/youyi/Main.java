package com.youyi;

import cn.hutool.core.io.FileUtil;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class Main {

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String tmpDirPath = String.format("%s/.tmp/cache/%s", projectPath, 2);
        FileUtil.del(tmpDirPath);
    }

}
