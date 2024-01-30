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
     * @param src   模板文件输入路径
     * @param dest  输出路径
     * @param model 数据模型
     */
    public static void doGenerate(final String src, String dest, Object model)
            throws IOException, TemplateException {
        File templateFile = new File(src);

        // Configure
        Configuration cfg = getConfiguration(templateFile);

        // get template
        String templateName = templateFile.getName();
        Template template = cfg.getTemplate(templateName);

        // handle file is not exist
        if (!FileUtil.exist(dest)) {
            FileUtil.touch(dest);
        }

        // process
        Writer out = new FileWriter(dest);
        template.process(model, out);

        out.close();
    }

    private static Configuration getConfiguration(File templateFile) throws IOException {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        File templateDir = templateFile.getParentFile();
        cfg.setDirectoryForTemplateLoading(templateDir);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setNumberFormat("0.######");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
        return cfg;
    }
}
