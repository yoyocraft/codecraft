package io.github.dingxinliang88.cli.pattern;

/**
 * 接收者
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class Device {

    private final String name;

    public Device(String name) {
        this.name = name;
    }

    public void turnOn() {
        System.out.println(name + " turn on");
    }

    public void turnOff() {
        System.out.println(name + " turn off");
    }
}
