package com.juzi;

import com.juzi.cli.CommandExecutor;

/**
 * @author codejuzi
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