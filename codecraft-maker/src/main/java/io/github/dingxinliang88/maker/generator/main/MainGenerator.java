package io.github.dingxinliang88.maker.generator.main;

import freemarker.template.TemplateException;
import io.github.dingxinliang88.maker.meta.Meta;
import java.io.IOException;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MainGenerator extends GeneratorTemplate {

    public static void main(String[] args)
            throws TemplateException, IOException, InterruptedException {
        new MainGenerator().doGenerate();
    }


    @Override
    protected String buildJar(Meta meta, String outputPath)
            throws IOException, InterruptedException {
        // do nothing
        return "";
    }

    @Override
    protected void buildScript(String outputPath, String jarPath) {
        // do nothing
    }

    @Override
    protected void buildDist(String outputPath, String jarPath, String sourceCopyDestPath) {
        // do nothing
    }
}
