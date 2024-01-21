package io.github.dingxinliang88.maker.generator.file;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 核心生成器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class FileGenerator {

    /**
     * 生成（静态 + 动态）
     *
     * @param model 数据模型
     */
    public static void doGenerate(Object model) throws TemplateException, IOException {
        // 获取整个项目的根路径
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        // 输入路径
        final String src = new File(parentFile, "sample/acm-template").getAbsolutePath();
        // 输出路径：输出到项目的根路径
        StaticFileGenerator.copyFileByHutool(src, projectPath);

        final String dynamicFileSrc =
                projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicFileDest =
                projectPath + File.separator
                        + "acm-template/src/io/github/dingxinliang88/acm/MainTemplate.java";
        DynamicFileGenerator.doGenerate(dynamicFileSrc, dynamicFileDest, model);
    }
}
