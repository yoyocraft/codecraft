package ${basePackage}.generator;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
* 核心生成器
*/
public class MainGenerator {

    public static void doGenerate(Object model) throws TemplateException, IOException {
        String srcRootPath = "${fileConfig.src}";
        String destRootPath = "${fileConfig.dest}";

        String src;
        String dest;
    <#list fileConfig.files as fileInfo>
        src = new File(srcRootPath, "${fileInfo.src}").getAbsolutePath();
        dest = new File(destRootPath, "${fileInfo.dest}").getAbsolutePath();
        <#if fileInfo.generateType == "static">
        StaticGenerator.copyFileByHutool(src, dest);
        <#else>
        DynamicGenerator.doGenerate(src, dest, model);
        </#if>
    </#list>

    }
}
