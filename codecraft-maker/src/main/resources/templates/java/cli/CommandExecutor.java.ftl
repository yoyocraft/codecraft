package ${basePackage}.cli;

import ${basePackage}.cli.command.ConfigCommand;
import ${basePackage}.cli.command.GenerateCommand;
import ${basePackage}.cli.command.JsonGenerateCommand;
import ${basePackage}.cli.command.ListCommand;
import ${basePackage}.cli.valid.CommandPreParser;
import picocli.CommandLine;


@CommandLine.Command(name = "craft", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {

    private final CommandPreParser commandPreParser;

    private final CommandLine commandLine;

    {
        commandPreParser = new CommandPreParser();
        commandLine = new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ListCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new JsonGenerateCommand());
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
