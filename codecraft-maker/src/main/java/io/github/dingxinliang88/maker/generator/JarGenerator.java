package io.github.dingxinliang88.maker.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jar 包生成器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class JarGenerator {

    private static final Logger logger = LoggerFactory.getLogger(JarGenerator.class);

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
            logger.info(line);
        }

        // 等待命令执行完成
        int exitCode = process.waitFor();
        logger.info("exit code = {}", exitCode);
    }

}
