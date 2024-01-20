package io.github.dingxinliang88;

import io.github.dingxinliang88.cli.CommandExecutor;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class Main {
    public static void main(String[] args) {
//        args = new String[]{"generate",/* "-l", */"-a", "codejuzi", "-o", "hhh"};
//        args = new String[]{"config"};
//        args = new String[]{"list"};
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}