package io.github.dingxinliang88.cli.pattern;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class TurnOnCommand implements Command {
    private final Device device;

    public TurnOnCommand(Device device) {
        this.device = device;
    }

    @Override
    public void execute() {
        device.turnOn();
    }
}
