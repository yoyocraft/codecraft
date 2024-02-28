package io.github.dingxinliang88.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 静态代码文件生成
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class StaticGenerator {

    private static final Logger logger = LoggerFactory.getLogger(StaticGenerator.class);

    public static void main(String[] args) {
        // 获取整个项目的根路径
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        // 输入路径
        String inputPath = new File(parentFile, "sample/acm-template").getAbsolutePath();
        // 输出路径：输出到项目的根路径的 .tmp 目录下
        String outputPath = projectPath + "/.tmp";
        copyFileByHutool(inputPath, outputPath);
    }

    /**
     * 拷贝文件，将输入目录完整拷贝到输出目录下 {@link cn.hutool.core.io.FileUtil#copy(String, String, boolean)}
     *
     * @param inputPath  输入目录路径
     * @param outputPath 输出目录路径
     */
    public static void copyFileByHutool(final String inputPath, String outputPath) {
        FileUtil.copy(inputPath, outputPath, true);
    }

    /**
     * 递归拷贝文件，将输入目录完整拷贝到输出目录下
     *
     * @param inputPath  输入目录路径
     * @param outputPath 输出目录路径
     * @see StaticGenerator#copyFileByHutool(String, String)
     * @deprecated
     */
    @Deprecated
    public static void copyFileByRecursive(final String inputPath, String outputPath) {
        copyFileByRecursive(new File(inputPath), new File(outputPath));
    }

    /**
     * 文件A => 目录B，则文件A放在目录B下 文件A => 文件B，则文件A覆盖文件B 目录A => 目录B，则目录A放在目录B下
     * <p>
     * 思路：先创建目录，然后遍历目录内的文件，依次复制
     *
     * @param inputFile  输入目录文件
     * @param outputFile 输出目录文件
     */
    private static void copyFileByRecursive(final File inputFile, File outputFile) {
        // 区分是文件还是目录
        if (inputFile.isDirectory()) {
            // 如果是目录，首先创建目标目录
            File destOutputFile = new File(outputFile, inputFile.getName());
            if (!destOutputFile.exists()) {
                // noinspection ResultOfMethodCallIgnored
                destOutputFile.mkdirs();
            }
            // 获取目录下的所有文件和子目录
            File[] files = inputFile.listFiles();
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
            Path destPath = outputFile.toPath().resolve(inputFile.getName());
            try {
                Files.copy(inputFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                logger.error("文件拷贝失败");
                throw new IORuntimeException(e);
            }
        }
    }
}
