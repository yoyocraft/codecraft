package command;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class RemoteControl {

    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressBtn() {
        command.execute();
    }
}
