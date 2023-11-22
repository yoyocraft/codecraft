package com.juzi.cli.pattern;

/**
 * @author codejuzi
 */
public class CommandClient {
    public static void main(String[] args) {
        // 创建接收者对象
        Device tv = new Device("tv");
        Device stereo = new Device("stereo");

        // 创建具体命令对象，绑定不同的设备
        TurnOnCommand turnOnCommand = new TurnOnCommand(tv);
        TurnOffCommand turnOffCommand = new TurnOffCommand(stereo);

        // 创建调用者
        RemoteControl remoteControl = new RemoteControl();

        // 执行命令
        remoteControl.setCommand(turnOnCommand);
        remoteControl.pressButton();

        remoteControl.setCommand(turnOffCommand);
        remoteControl.pressButton();
    }
}
