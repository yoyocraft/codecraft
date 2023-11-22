package com.juzi.cli;

import com.juzi.cli.command.ConfigCommand;
import com.juzi.cli.command.GenerateCommand;
import com.juzi.cli.command.ListCommand;
import com.juzi.cli.valid.CommandPreParser;
import picocli.CommandLine;

import java.util.Arrays;


/**
 * @author codejuzi
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
        System.out.println("请输入具体命令，或者输入 craft --help 查看命令提示");
    }

    /**
     * 执行命令
     *
     * @param args 参数
     * @return 退出码 exit code
     */
    public Integer doExecute(String[] args) {
        args = commandPreParser.parse(args);
        System.out.println(Arrays.toString(args));
        return commandLine.execute(args);
    }

}
