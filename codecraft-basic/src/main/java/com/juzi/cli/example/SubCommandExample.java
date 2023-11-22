package com.juzi.cli.example;

import picocli.CommandLine;

/**
 * 子命令示例
 *
 * @author codejuzi
 */
@CommandLine.Command(name = "main", mixinStandardHelpOptions = true)
public class SubCommandExample implements Runnable {


    @Override
    public void run() {
        System.out.println("execute main command");
    }


    @CommandLine.Command(name = "add", description = "add", mixinStandardHelpOptions = true)
    static class AddCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("execute add command");
        }
    }

    @CommandLine.Command(name = "delete", description = "delete", mixinStandardHelpOptions = true)
    static class DeleteCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("execute delete command");
        }
    }

    @CommandLine.Command(name = "query", description = "query", mixinStandardHelpOptions = true)
    static class QueryCommand implements Runnable {
        @Override
        public void run() {
            System.out.println("execute query command");
        }
    }

    public static void main(String[] args) {
        // execute main command
//        String[] myArgs = new String[]{};
        // main help
//        String[] myArgs = new String[]{"--help"};
        // execute add
//        String[] myArgs = new String[]{"add"};
        // add help
//        String[] myArgs = new String[]{"add", "--help"};
        // execute command which is not exist
        String[] myArgs = new String[]{"update"};
        int exitCode = new CommandLine(new SubCommandExample())
                .addSubcommand(new AddCommand())
                .addSubcommand(new DeleteCommand())
                .addSubcommand(new QueryCommand())
                .execute(myArgs);
        System.exit(exitCode);
    }
}
