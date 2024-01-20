package io.github.dingxinliang88.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * 静态代码文件生成
 *
 * @author codejuzi
 */
public class StaticGenerator {

    public static void main(String[] args) {
        // 获取整个项目的根路径
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        // 输入路径
        String src = new File(parentFile, "sample/acm-template").getAbsolutePath();
        // 输出路径：输出到项目的根路径
        String dest = projectPath;
//        copyFileByHutool(src, dest);
        copyFileByRecursive(src, dest);
    }

    /**
     * 拷贝文件，将输入目录完整拷贝到输出目录下
     * {@link cn.hutool.core.io.FileUtil#copy(String, String, boolean)}
     *
     * @param src  输入目录路径
     * @param dest 输出目录路径
     */
    public static void copyFileByHutool(final String src, String dest) {
        FileUtil.copy(src, dest, false);
    }

    /**
     * 递归拷贝文件，将输入目录完整拷贝到输出目录下
     *
     * @param src  输入目录路径
     * @param dest 输出目录路径
     */
    public static void copyFileByRecursive(final String src, String dest) {
        copyFileByRecursive(new File(src), new File(dest));
    }

    /**
     * 文件A => 目录B，则文件A放在目录B下
     * 文件A => 文件B，则文件A覆盖文件B
     * 目录A => 目录B，则目录A放在目录B下
     * <p>
     * 思路：先创建目录，然后遍历目录内的文件，依次复制
     *
     * @param src  输入目录路径
     * @param dest 输出目录路径
     */
    private static void copyFileByRecursive(final File src, File dest) {
        // 区分是文件还是目录
        if (src.isDirectory()) {
            // 如果是目录，首先创建目标目录
            File destOutputFile = new File(dest, src.getName());
            if (!destOutputFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                destOutputFile.mkdirs();
            }
            // 获取目录下的所有文件和子目录
            File[] files = src.listFiles();
            // 没有子文件，结束
            if (ArrayUtil.isEmpty(files)) {
                return;
            }
            for (File file : files) {
                // 递归拷贝下一层文件
                copyFileByRecursive(file, destOutputFile);
            }
        } else {
            // 是文件，直接复制到目录目录下
            Path destPath = dest.toPath().resolve(src.getName());
            try {
                Files.copy(src.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("文件拷贝失败");
                throw new IORuntimeException(e);
            }
        }
    }
}
