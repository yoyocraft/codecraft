package io.github.dingxinliang88.cli.command;

import cn.hutool.core.io.FileUtil;
import java.io.File;
import java.util.List;
import picocli.CommandLine;

/**
 * 展示文件命令
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@CommandLine.Command(name = "list", description = "list file", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {

    @Override
    public void run() {
        // 获取整个项目的根路径
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        // 输入路径
        String src = new File(parentFile, "sample/acm-template").getAbsolutePath();
        List<File> files = FileUtil.loopFiles(src);
        for (File file : files) {
            System.out.println(file.getAbsolutePath());
        }
    }
}
