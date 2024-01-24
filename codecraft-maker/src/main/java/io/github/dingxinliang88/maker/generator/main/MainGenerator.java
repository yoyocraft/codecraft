package io.github.dingxinliang88.maker.generator.main;

import freemarker.template.TemplateException;
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
    protected void buildDist(String outputPath, String jarPath, String srcCopyDestPath) {
        // do nothing
    }
}
