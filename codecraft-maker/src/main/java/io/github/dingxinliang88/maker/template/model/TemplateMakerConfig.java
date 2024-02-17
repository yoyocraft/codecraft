package io.github.dingxinliang88.maker.template.model;

import io.github.dingxinliang88.maker.meta.Meta;
import lombok.Data;

/**
 * 模板制作配置
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class TemplateMakerConfig {

    /**
     * 模板制作配置ID，可不传递，但一个制作过程如需要分步制作，需要保持一致的ID
     */
    private Long id;

    /**
     * 原始文件的路径
     */
    private String originProjectPath;

    /**
     * meta 信息
     */
    private Meta meta = new Meta();

    /**
     * 文件配置
     */
    private TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    /**
     * 模型配置
     */
    private TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

    /**
     * 输出配置
     */
    private TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();
}
