package io.github.dingxinliang88.model;

/**
 * 动态模板配置
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MainTemplateModel {

    /**
     * 是否生成循环
     */
    private boolean loop;

    /**
     * 作者注释
     */
    private String author = "youyi";

    /**
     * 输出信息
     */
    private String outputText = "Sum = ";


    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setOutputText(String outputText) {
        this.outputText = outputText;
    }

    // region setter for freemarker
    public boolean isLoop() {
        return loop;
    }

    public String getAuthor() {
        return author;
    }

    public String getOutputText() {
        return outputText;
    }

    // endregion

    @Override
    public String toString() {
        return "MainTemplateModel{" +
                "loop=" + loop +
                ", author='" + author + '\'' +
                ", outputText='" + outputText + '\'' +
                '}';
    }
}
