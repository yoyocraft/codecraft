package command;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class Device {

    private final String name;

    public Device(String name) {
        this.name = name;
    }

    public void turnOn() {
        System.out.println("Device " + name + " is turned on");
    }

    public void turnOff() {
        System.out.println("Device " + name + " is turned off");
    }

}
