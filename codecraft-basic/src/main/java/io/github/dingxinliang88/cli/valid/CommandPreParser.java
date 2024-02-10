package io.github.dingxinliang88.cli.valid;

import io.github.dingxinliang88.cli.CommandRegistry;
import picocli.CommandLine;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 命令前置转换器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class CommandPreParser {

    public String[] parse(String[] args) {
        if (args.length < 1) {
            return args;
        }
        String mainCommand = args[0];
        Optional<Class<?>> commandClassOpt = CommandRegistry.getCommandClass(mainCommand);
        if (commandClassOpt.isEmpty()) {
            return args;
        }
        // 保证命令的顺序
        Set<String> argsSet = new LinkedHashSet<>(Arrays.asList(args));

        for (Field field : commandClassOpt.get().getDeclaredFields()) {
            if (field.isAnnotationPresent(CommandLine.Option.class)) {
                String optionName = getOptionName(field);
                argsSet.add(optionName);
            }
        }

        return argsSet.toArray(new String[0]);
    }

    private String getOptionName(Field field) {
        CommandLine.Option option = field.getAnnotation(CommandLine.Option.class);
        if (option != null && option.names().length > 0) {
            return option.names()[0];
        }
        return null;
    }
}
