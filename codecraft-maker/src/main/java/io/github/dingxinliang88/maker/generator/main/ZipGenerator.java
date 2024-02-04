package io.github.dingxinliang88.maker.generator.main;

/**
 * 压缩包生成器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class ZipGenerator extends GeneratorTemplate {

    @Override
    protected String buildDist(String outputPath, String sourceCopyDestPath, String jarPath,
            String shellOutputFilePath) {
        String distOutputPath = super.buildDist(outputPath, sourceCopyDestPath, jarPath,
                shellOutputFilePath);
        return super.buildZip(distOutputPath);
    }
}
