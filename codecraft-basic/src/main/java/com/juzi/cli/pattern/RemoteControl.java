package com.juzi.cli.pattern;

/**
 * 调用者（遥控器）
 *
 * @author codejuzi
 */
public class RemoteControl {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton() {
        command.execute();
    }
}
