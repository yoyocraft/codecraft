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

    private Long id;

    private String originProjectPath;

    private Meta meta = new Meta();

    private TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    private TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();
}
