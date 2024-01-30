package io.github.dingxinliang88.maker.generator.file;

import cn.hutool.core.io.FileUtil;

/**
 * 静态代码文件生成
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class StaticFileGenerator {

    /**
     * 拷贝文件，将输入目录完整拷贝到输出目录下 {@link cn.hutool.core.io.FileUtil#copy(String, String, boolean)}
     *
     * @param src  输入目录路径
     * @param dest 输出目录路径
     */
    public static void copyFileByHutool(final String src, String dest) {
        FileUtil.copy(src, dest, true);
    }
}
