package ${basePackage}.cli;

import ${basePackage}.cli.command.ConfigCommand;
import ${basePackage}.cli.command.GenerateCommand;
import ${basePackage}.cli.command.ListCommand;
import ${basePackage}.cli.command.JsonGenerateCommand;

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
        COMMAND_MAP.put("json-generate", JsonGenerateCommand.class);
    }

    public static Optional<Class<?>> getCommandClass(String mainCommand) {
        return Optional.ofNullable(COMMAND_MAP.get(mainCommand));
    }

}