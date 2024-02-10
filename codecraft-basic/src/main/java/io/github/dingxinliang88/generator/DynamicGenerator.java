package io.github.dingxinliang88.generator;

import cn.hutool.core.io.FileUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.github.dingxinliang88.model.MainTemplateModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * 动态文件生成
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class DynamicGenerator {

    public static void main(String[] args) throws IOException, TemplateException {
        String projectPath = System.getProperty("user.dir");
        String inputSourceRoot = "";
        final String inputPath =
                inputSourceRoot + File.separator + "templates/MainTemplate.java.ftl";
        String outputPath =
                projectPath + File.separator + "src/main/resources/products/MainTemplate.java";
        // create model
        MainTemplateModel dataModel = new MainTemplateModel();
        dataModel.setLoop(true);
        dataModel.setAuthor("youyi");
        dataModel.setOutputText("Sum ==> ");
        doGenerate(inputPath, outputPath, dataModel);
    }

    /**
     * 生成文件
     *
     * @param inputFilePath  模板文件输入路径
     * @param outputFilePath 输出路径
     * @param model          数据模型
     * @see DynamicGenerator#doGenerate(String, String, Object)
     * @deprecated
     */
    @Deprecated
    public static void doGenerateByPath(final String inputFilePath, String outputFilePath,
            Object model) throws IOException, TemplateException {
        // Configure
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        File templateFile = new File(inputFilePath);
        File templateDir = templateFile.getParentFile();
        cfg.setDirectoryForTemplateLoading(templateDir);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setNumberFormat("0.######");

        // get template
        String templateName = templateFile.getName();
        Template template = cfg.getTemplate(templateName);

        // handle file is not exist
        if (!FileUtil.exist(outputFilePath)) {
            FileUtil.touch(outputFilePath);
        }

        // process
        Writer out = new FileWriter(outputFilePath);
        template.process(model, out);

        out.close();
    }

    /**
     * 使用相对路径生成文件
     *
     * @param relativeInputPath 模板文件相对输入路径
     * @param outputPath        输出路径
     * @param model             数据模型
     */
    public static void doGenerate(final String relativeInputPath, String outputPath, Object model)
            throws IOException, TemplateException {
        // 获取模板文件所属包和模板名称
        int lastSplitIndex = relativeInputPath.lastIndexOf("/");
        String basePackagePath = relativeInputPath.substring(0, lastSplitIndex);
        String templateName = relativeInputPath.substring(lastSplitIndex + 1);

        // 通过类加载器来读取模板
        ClassTemplateLoader templateLoader = new ClassTemplateLoader(
                DynamicGenerator.class, basePackagePath);

        // configure
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setDefaultEncoding("utf-8");
        configuration.setTemplateLoader(templateLoader);

        // get template
        Template template = configuration.getTemplate(templateName);

        // validate
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }

        // process
        Writer out = new FileWriter(outputPath);
        template.process(model, out);

        out.close();

    }
}
