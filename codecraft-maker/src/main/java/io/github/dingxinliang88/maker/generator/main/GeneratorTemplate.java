package io.github.dingxinliang88.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import freemarker.template.TemplateException;
import io.github.dingxinliang88.maker.generator.JarGenerator;
import io.github.dingxinliang88.maker.generator.ScriptGenerator;
import io.github.dingxinliang88.maker.generator.VersionControlGenerator;
import io.github.dingxinliang88.maker.generator.file.DynamicFileGenerator;
import io.github.dingxinliang88.maker.meta.Meta;
import io.github.dingxinliang88.maker.meta.MetaManager;
import java.io.File;
import java.io.IOException;

/**
 * 代码生成模板类
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public abstract class GeneratorTemplate {

    public void doGenerate() throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMeta();

        // 0. 输出根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath =
                projectPath + File.separator + "generated" + File.separator + meta.getName();
        // 清空目标目录
        if (FileUtil.exist(outputPath)) {
            FileUtil.del(outputPath);
        }
        FileUtil.mkdir(outputPath);

        // 读取 resources 目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        // 1. 复制原始文件
        String sourceCopyDestPath = copySource(meta, outputPath);

        // 2. 代码生成
        generateCode(meta, outputPath, inputResourcePath);

        // 3. 构建 Jar 包
        String jarPath = buildJar(meta, outputPath);

        // 4. 封装脚本
        buildScript(outputPath, jarPath);

        // 5. 版本控制
        versionControl(meta, inputResourcePath, outputPath);

        // 6. 生成精简版的程序（产物）
        buildDist(outputPath, jarPath, sourceCopyDestPath);
    }

    protected String copySource(Meta meta, String outputPath) {
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, true);
        return sourceCopyDestPath;
    }

    protected void generateCode(Meta meta, String outputPath, String inputResourcePath)
            throws IOException, TemplateException {

        // Java 包基础路径
        String outputBasePackage = meta.getBasePackage();
        String outputBasePackagePath = StrUtil.join(File.separator,
                StrUtil.split(outputBasePackage, "."));
        String outputBaseJavaPackagePath =
                outputPath + File.separator + "src/main/java/" + outputBasePackagePath;

        String inputFilePath;
        String outputFilePath;

        // model.DataModel
        inputFilePath =
                inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "model/DataModel.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ConfigCommand
        inputFilePath =
                inputResourcePath + File.separator
                        + "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.GenerateCommand
        inputFilePath =
                inputResourcePath + File.separator
                        + "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.command.ListCommand
        inputFilePath =
                inputResourcePath + File.separator
                        + "templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // cli.CommandExecutor
        inputFilePath =
                inputResourcePath + File.separator + "templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // Main
        inputFilePath = inputResourcePath + File.separator + "templates/java/Main.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + File.separator + "Main.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.DynamicGenerator
        inputFilePath =
                inputResourcePath + File.separator
                        + "templates/java/generator/DynamicGenerator.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.StaticGenerator
        inputFilePath =
                inputResourcePath + File.separator
                        + "templates/java/generator/StaticGenerator.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // generator.MainGenerator
        inputFilePath =
                inputResourcePath + File.separator
                        + "templates/java/generator/MainGenerator.java.ftl";
        outputFilePath =
                outputBaseJavaPackagePath + File.separator + "generator/MainGenerator.java";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // pom.xml
        inputFilePath = inputResourcePath + File.separator + "templates/pom.xml.ftl";
        outputFilePath = outputPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);

        // TODO 可以考虑让 AI 在生成文档的最后，再额外补充一段生成的话术，比如求 star 等等
        // README.md
        inputFilePath = inputResourcePath + File.separator + "templates/README.md.ftl";
        outputFilePath = outputPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerate(inputFilePath, outputFilePath, meta);
    }

    protected String buildJar(Meta meta, String outputPath)
            throws IOException, InterruptedException {
        JarGenerator.doGenerate(outputPath);
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(),
                meta.getVersion());
        return ".." + File.separator + "target" + File.separator + jarName;
    }

    protected void buildScript(String outputPath, String jarPath) {
        String shellOutputPath = outputPath + File.separator + "bin" + File.separator + "craft";
        ScriptGenerator.doGenerate(jarPath, shellOutputPath);
    }

    protected void versionControl(Meta meta, String inputResourcePath, String outputPath)
            throws IOException, InterruptedException {
        if (!meta.getVersionControl()) {
            return;
        }
        // 拷贝 .gitignore 文件
        String gitIgnorePath =
                inputResourcePath + File.separator + "templates" + File.separator + ".gitignore";
        FileUtil.copy(gitIgnorePath, outputPath, true);
        VersionControlGenerator.doGenerate(outputPath);
    }

    protected void buildDist(String outputPath, String jarPath, String sourceCopyDestPath) {
        String distDestPath = outputPath + "-dist";
        // 1. 拷贝 Jar 包
        String targetAbsolutePath = distDestPath + File.separator + "target";
        if (FileUtil.exist(targetAbsolutePath)) {
            FileUtil.del(targetAbsolutePath);
        }
        FileUtil.mkdir(targetAbsolutePath);
        String jarName = jarPath.substring(jarPath.lastIndexOf(File.separator) + 1);
        String jarAbsolutePath =
                outputPath + File.separator + "target" + File.separator + jarName;
        FileUtil.copy(jarAbsolutePath, targetAbsolutePath, true);
        // 2. 拷贝脚本文件
        String scriptAbsolutePath = distDestPath + File.separator;
        String scriptSrcPath = outputPath + File.separator + "bin" + File.separator;
        FileUtil.copy(scriptSrcPath, scriptAbsolutePath, true);
        // 3. 拷贝源模板文件
        FileUtil.copy(sourceCopyDestPath, distDestPath, true);
    }
}
