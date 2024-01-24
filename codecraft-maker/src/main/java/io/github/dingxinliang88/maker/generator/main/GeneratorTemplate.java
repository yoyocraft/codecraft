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
        String srcPath = classPathResource.getAbsolutePath();

        // 1. 复制原始文件
        String srcCopyDestPath = copySource(meta, outputPath);

        // 2. 代码生成
        generateCode(meta, outputPath, srcPath);

        // 3. 构建 Jar 包
        String jarPath = buildJar(meta, outputPath);

        // 4. 封装脚本
        buildScript(outputPath, jarPath);

        // 5. 版本控制
        versionControl(meta, srcPath, outputPath);

        // 6. 生成精简版的程序（产物）
        buildDist(outputPath, jarPath, srcCopyDestPath);
    }


    protected void versionControl(Meta meta, String srcPath, String outputPath)
            throws IOException, InterruptedException {
        if (!meta.getVersionControl()) {
            return;
        }
        // 拷贝 .gitignore 文件
        String gitIgnorePath =
                srcPath + File.separator + "templates" + File.separator + ".gitignore";
        FileUtil.copy(gitIgnorePath, outputPath, true);
        VersionControlGenerator.doGenerate(outputPath);
    }

    protected void buildDist(String outputPath, String jarPath, String srcCopyDestPath) {
        String distDestPath = outputPath + "--dist";
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
        FileUtil.copy(srcCopyDestPath, distDestPath, true);
    }

    protected void buildScript(String outputPath, String jarPath) {
        String shellOutputPath = outputPath + File.separator + "bin" + File.separator + "craft";
        ScriptGenerator.doGenerate(jarPath, shellOutputPath);
    }

    protected String buildJar(Meta meta, String outputPath)
            throws IOException, InterruptedException {
        JarGenerator.doGenerate(outputPath);
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(),
                meta.getVersion());
        return ".." + File.separator + "target" + File.separator + jarName;
    }

    protected void generateCode(Meta meta, String outputPath, String srcPath)
            throws IOException, TemplateException {

        // Java 包基础路径
        String outputBasePackage = meta.getBasePackage();
        String outputBasePackagePath = StrUtil.join(File.separator,
                StrUtil.split(outputBasePackage, "."));
        String outputBaseJavaPackagePath =
                outputPath + File.separator + "src" + File.separator + "main" + File.separator
                        + "java" + File.separator
                        + outputBasePackagePath;

        String src;
        String dest;

        // model.DataModel
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "model" + File.separator + "DataModel.java.ftl";
        dest = outputBaseJavaPackagePath + File.separator + "model" + File.separator
                + "DataModel.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // cli.command.ConfigCommand
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "cli" + File.separator + "command" + File.separator
                + "ConfigCommand.java.ftl";
        dest = outputBaseJavaPackagePath + File.separator + "cli" + File.separator + "command"
                + File.separator
                + "ConfigCommand.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // cli.command.GenerateCommand
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "cli" + File.separator + "command" + File.separator
                + "GenerateCommand.java.ftl";
        dest = outputBaseJavaPackagePath + File.separator + "cli" + File.separator + "command"
                + File.separator
                + "GenerateCommand.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // cli.command.ListCommand
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "cli" + File.separator + "command" + File.separator
                + "ListCommand.java.ftl";
        dest = outputBaseJavaPackagePath + File.separator + "cli" + File.separator + "command"
                + File.separator
                + "ListCommand.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // cli.CommandExecutor
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "cli" + File.separator + "CommandExecutor.java.ftl";
        dest = outputBaseJavaPackagePath + File.separator + "cli" + File.separator
                + "CommandExecutor.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // Main
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "Main.java.ftl";
        dest = outputBaseJavaPackagePath + File.separator + "Main.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // generator.DynamicGenerator
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "generator" + File.separator
                + "DynamicGenerator.java.ftl";
        dest = outputBaseJavaPackagePath + File.separator + "generator" + File.separator
                + "DynamicGenerator.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // generator.StaticGenerator
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "generator" + File.separator
                + "StaticGenerator.java.ftl";
        dest = outputBaseJavaPackagePath + File.separator + "generator" + File.separator
                + "StaticGenerator.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // generator.MainGenerator
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "generator" + File.separator + "MainGenerator.java.ftl";
        dest = outputBaseJavaPackagePath + File.separator + "generator" + File.separator
                + "MainGenerator.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // pom.xml
        src = srcPath + File.separator + "templates" + File.separator
                + "pom.xml.ftl";
        dest = outputPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // TODO 可以考虑让 AI 在生成文档的最后，再额外补充一段生成的话术，比如求 star 等等
        // README.md
        src = srcPath + File.separator + "templates" + File.separator
                + "README.md.ftl";
        dest = outputPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerate(src, dest, meta);
    }

    protected String copySource(Meta meta, String outputPath) {
        String srcRootPath = meta.getFileConfig().getSrcRootPath();
        String srcCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(srcRootPath, srcCopyDestPath, true);
        return srcCopyDestPath;
    }

}
