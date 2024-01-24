package io.github.dingxinliang88;

import io.github.dingxinliang88.cli.CommandExecutor;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class Main {

    public static void main(String[] args) {
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}