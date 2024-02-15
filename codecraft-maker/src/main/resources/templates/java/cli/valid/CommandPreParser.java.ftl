package ${basePackage}.cli.valid;

import ${basePackage}.cli.CommandRegistry;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import picocli.CommandLine;

/**
 * 命令前置转换器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class CommandPreParser {

    public String[] parse(String[] args) {
        if (args.length < 1 || !hasRegisteredCommand(args[0])) {
            return args;
        }

        Set<String> processedArgs = new LinkedHashSet<>(Arrays.asList(args));
        processOptionFields(processedArgs, getCommandClass(args[0]).orElse(null));

        return processedArgs.toArray(new String[0]);
    }

    private boolean hasRegisteredCommand(String mainCommand) {
        return CommandRegistry.getCommandClass(mainCommand).isPresent();
    }

    private Optional<Class<?>> getCommandClass(String mainCommand) {
        return CommandRegistry.getCommandClass(mainCommand);
    }

    private void processOptionFields(Set<String> argsSet, Class<?> commandClass) {
        if (commandClass != null) {
            Arrays.stream(commandClass.getDeclaredFields())
                    .filter(this::isAnnotatedWithRequiredOption)
                    .forEach(field -> addOptionNameToArgsSet(argsSet, field));
        }
    }

    private boolean isAnnotatedWithRequiredOption(Field field) {
        CommandLine.Option option = field.getAnnotation(CommandLine.Option.class);
        // 必需的参数
        return option != null && option.required() && option.names().length > 0;
    }

    private void addOptionNameToArgsSet(Set<String> argsSet, Field field) {
        CommandLine.Option option = field.getAnnotation(CommandLine.Option.class);
        String optionName = option.names()[0];
        argsSet.add(optionName);
    }
}
