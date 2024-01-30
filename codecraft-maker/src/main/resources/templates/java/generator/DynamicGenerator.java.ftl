package ${basePackage}.generator;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
* 动态文件生成
*/
public class DynamicGenerator {

    /**
    * 生成文件
    *
    * @param inputPath   模板文件输入路径
    * @param outputPath  输出路径
    * @param model 数据模型
    */
    public static void doGenerate(final String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        // Configure
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        File templateFile = new File(inputPath);
        File templateDir = templateFile.getParentFile();
        cfg.setDirectoryForTemplateLoading(templateDir);
        cfg.setDefaultEncoding("UTF-8");

        // get template
        String templateName = templateFile.getName();
        Template template = cfg.getTemplate(templateName);

        // handle file is not exist
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }

        // process
        Writer out = new FileWriter(outputPath);
        template.process(model, out);

        out.close();
    }
}
