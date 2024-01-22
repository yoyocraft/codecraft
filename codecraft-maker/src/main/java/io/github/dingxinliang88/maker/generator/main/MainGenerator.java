package io.github.dingxinliang88.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.sun.xml.internal.bind.v2.TODO;
import freemarker.template.TemplateException;
import io.github.dingxinliang88.maker.JarGenerator;
import io.github.dingxinliang88.maker.generator.ScriptGenerator;
import io.github.dingxinliang88.maker.generator.file.DynamicFileGenerator;
import io.github.dingxinliang88.maker.meta.Meta;
import io.github.dingxinliang88.maker.meta.MetaManager;
import java.io.File;
import java.io.IOException;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MainGenerator {

    public static void main(String[] args)
            throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMeta();

        // 输出根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath =
                projectPath + File.separator + "generated" + File.separator + meta.getName();

        // 清空目标目录
        if (FileUtil.exist(outputPath)) {
            FileUtil.del(outputPath);
        }
        FileUtil.mkdir(outputPath);

        // 复制原始文件
        String srcRootPath = meta.getFileConfig().getSrcRootPath();
        String srcCopyDestPath = outputPath + File.separator + ".source";
        FileUtil.copy(srcRootPath, srcCopyDestPath, true);

        // 读取 resources 目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String srcPath = classPathResource.getAbsolutePath();

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

        // 构建 Jar 包
        JarGenerator.doGenerate(outputPath);

        // 封装脚本
        String shellOutputPath = outputPath + File.separator + "bin" + File.separator + "craft";
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(),
                meta.getVersion());
        String jarPath = ".." + File.separator + "target" + File.separator + jarName;
        ScriptGenerator.doGenerate(jarPath, shellOutputPath);

        // 生成精简版的程序（产物）
        String distDestPath = outputPath + "--dist";
        // 1. 拷贝 Jar 包
        String targetAbsolutePath = distDestPath + File.separator + "target";
        FileUtil.mkdir(targetAbsolutePath);
        String jarAbsolutePath =
                outputPath + File.separator + "target" + File.separator + jarName;
        FileUtil.copy(jarAbsolutePath, targetAbsolutePath, true);
        // 2. 拷贝脚本文件
        String scriptAbsolutePath = distDestPath + File.separator;
        String scriptSrcPath = outputPath + File.separator + "bin" + File.separator;
        FileUtil.copy(scriptSrcPath, scriptAbsolutePath, true);
        // 3. 拷贝源模板文件
        FileUtil.copy(srcCopyDestPath, distDestPath, true);

        /*
        TODO 支持 Git 托管项目
            制作工具生成的代码生成器支持使用 git 版本控制工具来托管，可以根据元信息配置让开发者选择是否开启该特性。
            实现思路：通过 Process 执行 git init 命令，并复制 .gitignore 模板文件到代码生成器中
         */
    }

}
