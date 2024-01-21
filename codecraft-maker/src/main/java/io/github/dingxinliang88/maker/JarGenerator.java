package io.github.dingxinliang88.maker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class JarGenerator {

    public static void doGenerate(String projectDir) throws IOException, InterruptedException {
        // 清理之前的构建并打包
        String winMavenCommand = "mvn.cmd clean package -DskipTests=true";
        String otherMavenCommand = "mvn clean package -DskipTests=true";
        String mavenCommand =
                System.getProperty("os.name").toLowerCase().contains("windows") ? winMavenCommand
                        : otherMavenCommand;

        ProcessBuilder processBuilder = new ProcessBuilder(mavenCommand.split(" "));
        processBuilder.directory(new File(projectDir));

        Process process = processBuilder.start();

        // 读取命令的输出
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // 等待命令执行完成
        int exitCode = process.waitFor();
        System.out.println("命令执行结束，exitCode = " + exitCode);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        doGenerate(
                "/Users/codejuzi/Documents/CodeWorkSpace/Project/CodeCraft/codecraft/codecraft-maker/generated/acm-template-pro-generator");
    }

}
