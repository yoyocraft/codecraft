<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
<#-- 处理包名配置 -->
<#if fileInfo.outputPath?index_of("{") != -1 && fileInfo.outputPath?index_of("}") != -1>
<#assign input = fileInfo.outputPath>
<#assign openBracketIndex = input?index_of("{")>
<#assign closeBracketIndex = input?index_of("}")>
<#assign basePackage = input?substring(openBracketIndex + 1, closeBracketIndex)>
${indent}outputPath = new File(outputRootPath, "${fileInfo.outputPath}".replace("{basePackage}", basePackage.replace(".","/"))).getAbsolutePath();
<#else>
${indent}outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
</#if>
<#if fileInfo.generateType == "static">
${indent}StaticGenerator.copyFileByHutool(inputPath, outputPath);
<#else>
${indent}DynamicGenerator.doGenerate(inputPath, outputPath, model);
</#if>
</#macro>
package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;


/**
 * 核心生成器
 */
public class MainGenerator {

    public static void doGenerate(DataModel model) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;

    <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#-- 有分组 -->
        <#list modelInfo.models as subModelInfo>
        ${subModelInfo.type} ${subModelInfo.fieldName} = model.${modelInfo.groupKey}.${subModelInfo.fieldName};
        </#list>
        <#else>
        <#-- 无分组 -->
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
        </#if>
    </#list>

    <#list fileConfig.files as fileInfo>
        <#if fileInfo.groupKey??>
        <#-- 有分组，遍历分组下的文件 -->
        // groupKey: ${fileInfo.groupKey}
        <#if fileInfo.condition??>
        if(${fileInfo.condition}) {
            <#list fileInfo.files as fileInfo>
            <@generateFile fileInfo=fileInfo indent="            " />
            </#list>
        }
        <#else>
        <#list fileInfo.files as fileInfo>
        <@generateFile fileInfo=fileInfo indent="        " />
        </#list>
        </#if>
        <#else>
        <#-- 不是文件组，直接生成对应生成语句 -->
        <#if fileInfo.condition??>
        if(${fileInfo.condition}) {
            <@generateFile fileInfo=fileInfo indent="            " />
        }
        <#else>
        <@generateFile fileInfo=fileInfo indent="        " />
        </#if>
        </#if>
    </#list>
    }
}
