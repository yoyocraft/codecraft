package command;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class Client {

    public static void main(String[] args) {
        Device device = new Device("tv");

        RemoteControl remoteControl = new RemoteControl();

        remoteControl.setCommand(new TurnOnCommand(device));
        remoteControl.pressBtn();

        remoteControl.setCommand(new TurnOffCommand(device));
        remoteControl.pressBtn();
    }

}
