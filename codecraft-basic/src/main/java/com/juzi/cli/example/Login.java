package com.juzi.cli.example;


import picocli.CommandLine;

import java.util.concurrent.Callable;

class Login implements Callable<Integer> {
    @CommandLine.Option(names = {"-u", "--user"}, description = "User name")
    String user;

    @CommandLine.Option(names = {"-p", "--password"}, arity = "0..1", description = "Passphrase", interactive = true)
    String password;

    @CommandLine.Option(names = {"-cp", "--checkPassword"}, description = "Check Password", interactive = true)
    String checkPassword;


    public Integer call() throws Exception {
        System.out.println("user = " + user);
        System.out.println("password = " + password);
        System.out.println("checkPassword = " + checkPassword);
        return 0;
    }

    public static void main(String[] args) {
        new CommandLine(new Login()).execute("-u", "juzi", "-p", "juzi", "-cp");
    }

}