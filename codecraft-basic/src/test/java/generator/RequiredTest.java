package generator;

import io.github.dingxinliang88.cli.command.ConfigCommand;
import io.github.dingxinliang88.cli.command.GenerateCommand;
import io.github.dingxinliang88.cli.command.ListCommand;
import io.github.dingxinliang88.cli.utils.CommandUtils;
import org.junit.Test;
import picocli.CommandLine;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class RequiredTest {

    @Test
    public void testRequired() {
        Field[] fields = GenerateCommand.class.getDeclaredFields();
        for (Field field : fields) {
            CommandLine.Option option = field.getAnnotation(CommandLine.Option.class);
            if (option != null) {
                String name = option.names()[0];
                boolean required = option.required();
                System.out.println(name + " => " + required);
            }
        }
    }

    static final Map<String, Class<?>> COMMAND_MAP = new HashMap<String, Class<?>>() {{
        put("generate", GenerateCommand.class);
        put("list", ListCommand.class);
        put("config", ConfigCommand.class);
    }};

    @Test
    public void testPreParser() {
        String[] args = new String[]{"generate"};
        // 需要根据mainCommand寻找相关的命令
        String mainCommand = args[0];
        Class<?> commandClass = COMMAND_MAP.get(mainCommand);
        if (commandClass == null) return;
        ArrayList<String> argsList = new ArrayList<>(Arrays.asList(args));
        Field[] fields = commandClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(CommandLine.Option.class)) {
                String optionName = getOptionName(field);
                if (!argsList.contains(optionName)) {
                    argsList.add(optionName);
                }
            }
        }
        System.out.println(argsList);
    }

    @Test
    public void testPackage() {
        Package commandPackage = GenerateCommand.class.getPackage();
        String packageName = commandPackage.getName();
        System.out.println(packageName);
    }

    @Test
    public void testCommandUtils() {
        Optional<Class<?>> commandOpt = CommandUtils.getCommandClass("generate");
        if (commandOpt.isPresent()) {
            Class<?> commandClass = commandOpt.get();
            System.out.println(commandClass);
        } else {
            System.out.println("命令不存在！");
        }
    }


    private String getOptionName(Field field) {
        CommandLine.Option option = field.getAnnotation(CommandLine.Option.class);
        if (option != null && option.names().length > 0) {
            return option.names()[0];
        }
        return null;
    }
}
