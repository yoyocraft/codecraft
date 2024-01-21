package io.github.dingxinliang88.cli.pattern;

/**
 * 调用者（遥控器）
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
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
