package io.github.dingxinliang88.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.TimeZone;

/**
 * 动态文件生成
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class DynamicFileGenerator {

    /**
     * 生成文件
     *
     * @param inputPath   模板文件输入路径
     * @param outputPath  输出路径
     * @param model 数据模型
     */
    public static void doGenerate(final String inputPath, String outputPath, Object model)
            throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);
        configuration.setDefaultEncoding("utf-8");
        String templateName = new File(inputPath).getName();
        Template template = configuration.getTemplate(templateName);

        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }

        // process
        Writer out = new FileWriter(outputPath);
        template.process(model, out);

        out.close();
    }
}
