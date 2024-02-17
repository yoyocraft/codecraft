<#-- 生成选项 -->
<#macro generateOption indent modelInfo>
${indent}@CommandLine.Option(
        ${indent}names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}", </#if>"--${modelInfo.fieldName}"},
        ${indent}arity = "0..1",
        ${indent}<#if modelInfo.description??>description = "${modelInfo.description}",</#if>
        ${indent}interactive = true,<#if modelInfo.required??>
        ${indent}required = true,</#if>
        ${indent}echo = true)
${indent}private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#macro>

<#-- 生成命令调用 -->
<#macro generateCommand indent modelInfo>
${indent}System.out.println("输入${modelInfo.groupName}配置：");
${indent}CommandLine ${modelInfo.groupKey}CommandLine = new CommandLine(${modelInfo.type}Command.class);
${indent}${modelInfo.groupKey}CommandLine.execute(${modelInfo.allArgsStr});
</#macro>
package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@lombok.Data
@CommandLine.Command(name = "generate", description = "generate code", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {
<#list modelConfig.models as modelInfo>
    <#-- 有分组 -->
    <#if modelInfo.groupKey??>
    static DataModel.${modelInfo.type} ${modelInfo.groupKey} = new DataModel.${modelInfo.type}();

    <#-- 生成命令类 -->
    @lombok.Data
    @CommandLine.Command(name = "${modelInfo.groupKey}")
    public static class ${modelInfo.type}Command implements Runnable {
      <#list modelInfo.models as subModelInfo>
          <@generateOption modelInfo=subModelInfo indent="        " />
      </#list>
        @Override
        public void run() {
        <#list modelInfo.models as subModelInfo>
            ${modelInfo.groupKey}.${subModelInfo.fieldName} = ${subModelInfo.fieldName};
        </#list>
        }
    }
    <#else>
    <@generateOption modelInfo=modelInfo indent="    " />
    </#if>
  </#list>

    <#-- 生成调用方法 -->
    @Override
    public Integer call() throws Exception {
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#if modelInfo.condition??>
        if(${modelInfo.condition}) {
            <@generateCommand modelInfo=modelInfo indent="            " />
        }
        <#else>
        <@generateCommand modelInfo=modelInfo indent="        " />
        </#if>
        </#if>
        </#list>
        <#-- 填充数据模型对象 -->
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        dataModel.${modelInfo.groupKey} = ${modelInfo.groupKey};
        </#if>
        </#list>
        MainGenerator.doGenerate(dataModel);
        return 0;
    }

}