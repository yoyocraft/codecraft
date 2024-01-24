package io.github.dingxinliang88.maker.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class VersionControlGenerator {

    public static void doGenerate(String projectDir) throws IOException, InterruptedException {
        String command = "git init";
        Process process = new ProcessBuilder(command.split(" "))
                .directory(new File(projectDir)).start();

        // 等待命令执行完成
        process.waitFor();

        command = "git add .";
        process = new ProcessBuilder(command.split(" "))
                .directory(new File(projectDir)).start();
        // 读取命令的输出
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        process.waitFor();
    }
}
