package io.github.dingxinliang88.maker;

import freemarker.template.TemplateException;
import io.github.dingxinliang88.maker.generator.main.GeneratorTemplate;
import io.github.dingxinliang88.maker.generator.main.SrcZipGenerator;
import java.io.IOException;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class Main {

    public static void main(String[] args)
            throws TemplateException, IOException, InterruptedException {
        GeneratorTemplate generatorTemplate = new SrcZipGenerator();
        generatorTemplate.doGenerate();
    }

}
