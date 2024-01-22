package io.github.dingxinliang88.maker.generator.main;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
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

        String projectPath = System.getProperty("user.dir");
        String projectDestPath =
                projectPath + File.separator + "generated" + File.separator + meta.getName();

        // 清空目标目录
        if (FileUtil.exist(projectDestPath)) {
            FileUtil.del(projectDestPath);
        }
        FileUtil.mkdir(projectDestPath);

        // 复制原始文件
        String srcRootPath = meta.getFileConfig().getSrcRootPath();
        String scrCopyDestPath = projectDestPath + File.separator + ".source";
        FileUtil.copy(srcRootPath, scrCopyDestPath, false);

        // 读取 resources 目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String srcPath = classPathResource.getAbsolutePath();

        // Java 包基础路径
        String basePackagePath = meta.getBasePackage();
        String destBasePackagePath = StrUtil.join(File.separator,
                StrUtil.split(basePackagePath, "."));
        String destBaseJavaPackagePath =
                projectDestPath + File.separator + "src" + File.separator + "main" + File.separator
                        + "java" + File.separator
                        + destBasePackagePath;

        // model.DataModel
        String src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "model" + File.separator + "DataModel.java.ftl";
        String dest = destBaseJavaPackagePath + File.separator + "model" + File.separator
                + "DataModel.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // cli.command.ConfigCommand
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "cli" + File.separator + "command" + File.separator
                + "ConfigCommand.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "cli" + File.separator + "command"
                + File.separator
                + "ConfigCommand.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // cli.command.GenerateCommand
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "cli" + File.separator + "command" + File.separator
                + "GenerateCommand.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "cli" + File.separator + "command"
                + File.separator
                + "GenerateCommand.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // cli.command.ListCommand
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "cli" + File.separator + "command" + File.separator
                + "ListCommand.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "cli" + File.separator + "command"
                + File.separator
                + "ListCommand.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // cli.CommandExecutor
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "cli" + File.separator + "CommandExecutor.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "cli" + File.separator
                + "CommandExecutor.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // Main
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "Main.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "Main.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // generator.DynamicGenerator
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "generator" + File.separator
                + "DynamicGenerator.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "generator" + File.separator
                + "DynamicGenerator.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // generator.StaticGenerator
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "generator" + File.separator
                + "StaticGenerator.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "generator" + File.separator
                + "StaticGenerator.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // generator.MainGenerator
        src = srcPath + File.separator + "templates" + File.separator
                + "java" + File.separator + "generator" + File.separator + "MainGenerator.java.ftl";
        dest = destBaseJavaPackagePath + File.separator + "generator" + File.separator
                + "MainGenerator.java";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // pom.xml
        src = srcPath + File.separator + "templates" + File.separator
                + "pom.xml.ftl";
        dest = projectDestPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerate(src, dest, meta);

        // TODO 可以考虑让 AI 在生成文档的最后，再额外补充一段生成的话术，比如求 star 等等
        // README.md
        src = srcPath + File.separator + "templates" + File.separator
                + "README.md.ftl";
        dest = projectDestPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerate(src, dest, meta);


        // 构建 Jar 包
        JarGenerator.doGenerate(projectDestPath);

        // 封装脚本
        String shellDest = projectDestPath + File.separator + "bin" + File.separator + "craft";
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(),
                meta.getVersion());
        String jarPath = projectDestPath + File.separator + "target" + File.separator + jarName;
        ScriptGenerator.doGenerate(jarPath, shellDest);
    }

}
