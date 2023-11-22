package com.juzi.cli.utils;


import com.juzi.cli.command.ConfigCommand;
import com.juzi.cli.command.GenerateCommand;
import com.juzi.cli.command.ListCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandUtils {

    public static final Map<String, Class<?>> COMMAND_MAP;

    static {
        COMMAND_MAP = new HashMap<>();
        COMMAND_MAP.put("generate", GenerateCommand.class);
        COMMAND_MAP.put("list", ListCommand.class);
        COMMAND_MAP.put("config", ConfigCommand.class);
    }

    public static Optional<Class<?>> getCommandClass(String mainCommand) {
        return Optional.ofNullable(COMMAND_MAP.get(mainCommand));
    }

}