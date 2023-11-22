package com.juzi.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.juzi.model.MainTemplateModel;
import picocli.CommandLine;

import java.lang.reflect.Field;

/**
 * @author codejuzi
 */
@CommandLine.Command(name = "config", description = "model config", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {

    @Override
    public void run() {
        System.out.println("查看参数信息");
//        Field[] fields = MainTemplateModel.class.getDeclaredFields();
        Field[] fields = ReflectUtil.getFields(MainTemplateModel.class);
        for (Field field : fields) {
            System.out.printf("- [%s] %s%n", field.getType(), field.getName());
        }
    }
}
