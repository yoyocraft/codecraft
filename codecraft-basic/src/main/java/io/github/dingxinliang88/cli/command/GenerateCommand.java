package io.github.dingxinliang88.cli.command;

import cn.hutool.core.bean.BeanUtil;
import io.github.dingxinliang88.generator.MainGenerator;
import io.github.dingxinliang88.model.MainTemplateModel;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@CommandLine.Command(name = "generate", description = "generate code", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {

    /**
     * 是否生成循环
     */
    @CommandLine.Option(
            names = {"-l", "--loop"},
            arity = "0..1",
            description = "是否有循环体",
            required = true,
            interactive = true,
            echo = true)
    private boolean loop;

    /**
     * 作者注释
     */
    @CommandLine.Option(
            names = {"-a", "--author"},
            arity = "0..1",
            description = "作者",
            interactive = true,
            echo = true)
    private String author = "youyi";

    /**
     * 输出信息
     */
    @CommandLine.Option(
            names = {"-o", "--outputText"},
            arity = "0..1",
            description = "输出文本",
            interactive = true,
            echo = true)
    private String outputText = "Sum = ";

    @Override
    public Integer call() throws Exception {
        MainTemplateModel mainTemplateModel = new MainTemplateModel();
        BeanUtil.copyProperties(this, mainTemplateModel);
        System.out.println("配置信息 = " + mainTemplateModel);
        MainGenerator.doGenerate(mainTemplateModel);
        return 0;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOutputText() {
        return outputText;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }
}
