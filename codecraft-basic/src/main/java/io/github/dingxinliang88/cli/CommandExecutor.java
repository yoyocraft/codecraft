package io.github.dingxinliang88.cli;

import io.github.dingxinliang88.cli.command.ConfigCommand;
import io.github.dingxinliang88.cli.command.GenerateCommand;
import io.github.dingxinliang88.cli.command.ListCommand;
import io.github.dingxinliang88.cli.valid.CommandPreParser;
import picocli.CommandLine;


/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@CommandLine.Command(name = "craft", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {

    private final CommandPreParser commandPreParser;
    private final CommandLine commandLine;

    {
        commandPreParser = new CommandPreParser();
        commandLine = new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ListCommand())
                .addSubcommand(new ConfigCommand());
    }

    @Override
    public void run() {
        // 不输入子命令时，给出友好提示
        commandLine.usage(System.out);
    }

    /**
     * 执行命令
     *
     * @param args 参数
     * @return 退出码 exit code
     */
    public Integer doExecute(String[] args) {
        args = commandPreParser.parse(args);
        return commandLine.execute(args);
    }

}
