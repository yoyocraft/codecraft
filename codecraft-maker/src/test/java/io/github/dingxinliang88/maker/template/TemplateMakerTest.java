package io.github.dingxinliang88.maker.template;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import io.github.dingxinliang88.maker.meta.Meta;
import io.github.dingxinliang88.maker.meta.enums.ModelTypeEnum;
import io.github.dingxinliang88.maker.template.model.TemplateMakerConfig;
import io.github.dingxinliang88.maker.template.model.TemplateMakerFileConfig;
import io.github.dingxinliang88.maker.template.model.TemplateMakerModelConfig;
import java.io.File;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class TemplateMakerTest {

    @Test
    public void testMakeTemplate1() {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath =
                new File(projectPath).getParent() + File.separator + "sample/springboot-init";

        // 文件参数配置
        String inputFilePath1 = "src/main/resources/application.yml";
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();

        // 文件配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        templateMakerFileConfig.setFiles(Collections.singletonList(fileInfoConfig1));

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        // 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType(ModelTypeEnum.STRING.getValue());
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Collections.singletonList(
                modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        long id = TemplateMaker.makeTemplate(meta, originProjectPath,
                templateMakerFileConfig, templateMakerModelConfig, null,
                1752657938160234496L);
        log.info("生成的模板id为:{}", id);
    }

    @Test
    public void testMakeTemplate2() {
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 示例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath =
                new File(projectPath).getParent() + File.separator + "sample/springboot-init";

        // 文件参数配置
        String inputFilePath1 = "src/main/java/com/youyi/springbootinit/common";
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        templateMakerFileConfig.setFiles(Collections.singletonList(fileInfoConfig1));

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("className");
        modelInfoConfig1.setType(ModelTypeEnum.STRING.getValue());
        modelInfoConfig1.setReplaceText("BaseResponse");
        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList
                = Collections.singletonList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        Long id = TemplateMaker.makeTemplate(meta, originProjectPath,
                templateMakerFileConfig, templateMakerModelConfig, null,
                1752657938160234496L);
        log.info("id = {}", id);
    }

    @Test
    public void testMakeTemplateWithJSON() {
        String configJsonStr = ResourceUtil.readUtf8Str("template-maker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        Long id = TemplateMaker.makeTemplate(templateMakerConfig);
        log.info("id = {}", id);
    }


    @Test
    public void makeSpringBootTemplate() {
        String rootPath = "example/springboot-init-plus/";
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

    @Test
    public void makeHappyBirthdayTemplate() {
        String rootPath = "example/happy-birthday/";
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

        log.info("id = {}", id);
    }

    @Test
    public void makeMyCardTemplate() {
        String rootPath = "example/my-card/";
        String configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker0.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        Long id = TemplateMaker.makeTemplate(templateMakerConfig);

        configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker1.json");
        templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        log.info("id = {}", id);
    }

    @Test
    public void makeLoveTemplate() {
        String rootPath = "example/love/";
        String configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker0.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        Long id = TemplateMaker.makeTemplate(templateMakerConfig);

        configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker1.json");
        templateMakerConfig = JSONUtil.toBean(configJsonStr,
                TemplateMakerConfig.class);
        TemplateMaker.makeTemplate(templateMakerConfig);

        log.info("id = {}", id);
    }
}