package io.github.dingxinliang88.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import java.io.File;

/**
 * 压缩包生成器，包含 src、pom.xml、README.md 等文件
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class SrcZipGenerator extends GeneratorTemplate {

    @Override
    protected String buildDist(String outputPath, String sourceCopyDestPath, String jarPath,
            String shellOutputFilePath) {
        String distOutputPath = super.buildDist(outputPath, sourceCopyDestPath, jarPath,
                shellOutputFilePath);
        // 拷贝 src、pom.xml、README.md等文件
        FileUtil.copy(outputPath + File.separator + "src", distOutputPath, true);
        FileUtil.copy(outputPath + File.separator + "pom.xml", distOutputPath, true);
        FileUtil.copy(outputPath + File.separator + "README.md", distOutputPath, true);
        return super.buildZip(distOutputPath);
    }
}
