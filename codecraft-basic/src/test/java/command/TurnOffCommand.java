package command;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class TurnOffCommand implements Command {

    private final Device device;

    public TurnOffCommand(Device device) {
        this.device = device;
    }

    @Override
    public void execute() {
        device.turnOff();
    }
}
