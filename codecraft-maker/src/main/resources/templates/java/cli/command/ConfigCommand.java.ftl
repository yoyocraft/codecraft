package ${basePackage}.cli.command;

import cn.hutool.core.util.ReflectUtil;
import ${basePackage}.model.DataModel;
import picocli.CommandLine;

import java.lang.reflect.Field;

@CommandLine.Command(name = "config", description = "model config", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("查看参数信息");
        Field[] fields = ReflectUtil.getFields(DataModel.class);
        for (Field field : fields) {
            System.out.printf("- [%s] %s%n", field.getType(), field.getName());
        }
    }
}
