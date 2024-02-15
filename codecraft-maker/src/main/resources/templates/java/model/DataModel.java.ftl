<#-- 生成模型字段，包含注释和默认值，访问类型为 public，便于后续操作-->
<#macro generateModel indent modelInfo>
<#if modelInfo.description??>
${indent}/**
${indent} * ${modelInfo.description}
${indent} */
</#if>
${indent}public ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#macro>
package ${basePackage}.model;

import lombok.Data;

/**
 * 数据模型
 */
@Data
public class DataModel {
<#list modelConfig.models as modelInfo>
    <#if modelInfo.groupKey??>
    <#-- 有分组 -->
    /**
     * ${modelInfo.groupName}
     */
    public ${modelInfo.type} ${modelInfo.groupKey} = new ${modelInfo.type}();

    /**
     * ${modelInfo.description}
     */
    @Data
    public static class ${modelInfo.type} {
    <#list modelInfo.models as modelInfo>
        <@generateModel modelInfo=modelInfo indent="        " />
    </#list>
    }
    <#else>
    <#-- 无分组 -->
    <@generateModel modelInfo=modelInfo indent="    " />
    </#if>
</#list>
}