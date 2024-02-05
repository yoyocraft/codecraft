package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@lombok.Data
@CommandLine.Command(name = "json-generate", description = "generate code from json file", mixinStandardHelpOptions = true)
public class JsonGenerateCommand implements Callable<Integer> {

    @CommandLine.Option(
            names = {"-f", "--file"},
            arity = "0..1",
            description = "json 文件路径",
            interactive = true,
            echo = true)
    private String filePath;


    @Override
    public Integer call() throws Exception {
        String modelJsonStr = FileUtil.readUtf8String(filePath);
        DataModel dataModel = JSONUtil.toBean(modelJsonStr, DataModel.class);
        MainGenerator.doGenerate(dataModel);
        return 0;
    }

}