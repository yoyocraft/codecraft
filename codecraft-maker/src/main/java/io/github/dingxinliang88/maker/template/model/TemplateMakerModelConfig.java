package io.github.dingxinliang88.maker.template.model;

import io.github.dingxinliang88.maker.meta.Meta;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class TemplateMakerModelConfig {


    private List<ModelInfoConfig> models;

    private ModelGroupConfig modelGroupConfig;

    @NoArgsConstructor
    @Data
    public static class ModelInfoConfig {

        private String fieldName;

        private String type;

        private String description;

        private Object defaultValue;

        private String abbr;

        // 替换文本
        private String replaceText;

    }

    @NoArgsConstructor
    @Data
    public static class ModelGroupConfig {

        private String condition;

        private String groupKey;

        private String groupName;

        private String type;

        private String description;
    }
}
