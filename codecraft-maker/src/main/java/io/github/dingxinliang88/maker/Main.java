package io.github.dingxinliang88.maker;

import freemarker.template.TemplateException;
import io.github.dingxinliang88.maker.generator.main.MainGenerator;
import java.io.IOException;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class Main {

    public static void main(String[] args)
            throws TemplateException, IOException, InterruptedException {
        new MainGenerator().doGenerate();
    }

}
