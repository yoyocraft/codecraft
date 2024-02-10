package io.github.dingxinliang88.cli.command;

import cn.hutool.core.util.ReflectUtil;
import io.github.dingxinliang88.model.MainTemplateModel;
import picocli.CommandLine;

import java.lang.reflect.Field;

/**
 * 配置类命令
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@CommandLine.Command(name = "config", description = "model config", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("查看参数信息");
        Field[] fields = ReflectUtil.getFields(MainTemplateModel.class);
        for (Field field : fields) {
            System.out.printf("- [%s] %s%n", field.getType(), field.getName());
        }
    }
}
