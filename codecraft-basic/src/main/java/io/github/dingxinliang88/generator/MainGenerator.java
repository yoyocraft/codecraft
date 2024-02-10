package io.github.dingxinliang88.generator;

import io.github.dingxinliang88.model.MainTemplateModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 核心生成器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MainGenerator {

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
        final String inputPath = new File(parentFile, "sample/acm-template").getAbsolutePath();
        String outputPath = projectPath + File.separator + ".tmp";
        // 输出路径：输出到项目的根路径
        StaticGenerator.copyFileByHutool(inputPath, outputPath);

        // 动态模板生成
        String inputSourceRoot = "";
        final String inputDynamicFilePath =
                inputSourceRoot + File.separator + "templates/MainTemplate.java.ftl";
        String outputDynamicFilePath = projectPath + File.separator
                + ".tmp/acm-template/src/io/github/dingxinliang88/acm/MainTemplate.java";
        DynamicGenerator.doGenerate(inputDynamicFilePath, outputDynamicFilePath, model);
    }

    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateModel dataModel = new MainTemplateModel();
        dataModel.setLoop(true);
        dataModel.setAuthor("youyichannel");
        dataModel.setOutputText("Sum ==> ");
        doGenerate(dataModel);
    }
}
