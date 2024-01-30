package ${basePackage}.generator;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
* 核心生成器
*/
public class MainGenerator {

    public static void doGenerate(Object model) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;
    <#list fileConfig.files as fileInfo>
        inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
        outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
        <#if fileInfo.generateType == "static">
        StaticGenerator.copyFileByHutool(inputPath, outputPath);
        <#else>
        DynamicGenerator.doGenerate(inputPath, outputPath, model);
        </#if>
    </#list>

    }
}
