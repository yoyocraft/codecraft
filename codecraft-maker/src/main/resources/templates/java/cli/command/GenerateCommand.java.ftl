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
    @CommandLine.Option(
    names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}"</#if>, "--${modelInfo.fieldName}"},
    arity = "0..1",
    <#if modelInfo.description??>description = "${modelInfo.description}",</#if>
    interactive = true,
    echo = true)
    private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
  </#list>


  @Override
  public Integer call() throws Exception {
      DataModel dataModel = new DataModel();
      BeanUtil.copyProperties(this, dataModel);
      System.out.println("配置信息 = " + dataModel);
      MainGenerator.doGenerate(dataModel);
      return 0;
  }

}