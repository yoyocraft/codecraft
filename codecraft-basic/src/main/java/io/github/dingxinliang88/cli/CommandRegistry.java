package io.github.dingxinliang88.cli;


import io.github.dingxinliang88.cli.command.ConfigCommand;
import io.github.dingxinliang88.cli.command.GenerateCommand;
import io.github.dingxinliang88.cli.command.ListCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


/**
 * 命令注册器类
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class CommandRegistry {

    public static final Map<String, Class<?>> COMMAND_MAP;

    static {
        COMMAND_MAP = new HashMap<>();
        COMMAND_MAP.put("generate", GenerateCommand.class);
        COMMAND_MAP.put("list", ListCommand.class);
        COMMAND_MAP.put("config", ConfigCommand.class);
    }

    public static Optional<Class<?>> getCommandClass(String mainCommand) {
        return Optional.ofNullable(COMMAND_MAP.get(mainCommand));
    }

}