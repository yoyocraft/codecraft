package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;


@CommandLine.Command(name = "list", description = "list file", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {

    @Override
    public void run() {
        // 输入路径
        String inputRootPath = "${fileConfig.sourceRootPath}";
        List<File> files = FileUtil.loopFiles(inputRootPath);
        for (File file : files) {
            System.out.println(file);
        }
    }
}
