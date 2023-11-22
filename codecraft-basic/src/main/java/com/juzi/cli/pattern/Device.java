package com.juzi.cli.pattern;

/**
 * 接收者
 *
 * @author codejuzi
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
