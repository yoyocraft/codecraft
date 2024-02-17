package io.github.dingxinliang88.demo.example;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import io.github.dingxinliang88.maker.template.TemplateMaker;
import io.github.dingxinliang88.maker.template.model.TemplateMakerConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * 模板制作示例
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class TemplateMakerExample {

    public static void main(String[] args) {
        makeSpringBootTemplate();
    }

    public static void makeSpringBootTemplate() {
        // 指定模板文件Json配置的根目录
        String rootPath = "example/springboot-init-plus/";

        // 分步制作
        String configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker0.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        Long id = TemplateMaker.makeTemplate(templateMakerConfig);

        configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker1.json");
        templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker2.json");
        templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker3.json");
        templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker4.json");
        templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker5.json");
        templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker6.json");
        templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker7.json");
        templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker8.json");
        templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        log.info("id = {}", id);
    }
}
