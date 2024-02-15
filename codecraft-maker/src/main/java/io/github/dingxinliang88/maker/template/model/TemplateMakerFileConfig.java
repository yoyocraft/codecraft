package io.github.dingxinliang88.maker.template.model;

import io.github.dingxinliang88.maker.template.enums.CodeSnippetCheckTypeEnum;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模板制作文件配置
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class TemplateMakerFileConfig {

    /*
        {
            "files": [
                {
                    "path": "文件（目录）路径",
                    "filterConfigList": [
                        {
                            "range": "fileName",
                            "rule": "regex",
                            "value": ".*lala.*"
                        },
                        {
                            "range": "fileContent",
                            "rule": "contains",
                            "value": "post"
                        },
                    ]
                }
            ]
        }
     */
    private List<FileInfoConfig> files;
    private FileGroupConfig fileGroupConfig;

    @NoArgsConstructor
    @Data
    public static class FileInfoConfig {

        private String path;

        private String condition;

        private List<FileFilterConfig> filterConfigList;

        /**
         * 代码片段配置
         */
        private List<CodeSnippetConfig> codeSnippetConfigList;

        /**
         * 转义转换配置
         */
        private CodeSnippetConfig noParseConfig;
    }

    @NoArgsConstructor
    @Data
    public static class FileGroupConfig {

        private String condition;

        private String groupKey;

        private String groupName;
    }

    @NoArgsConstructor
    @Data
    public static class CodeSnippetConfig {

        private String code;
        private String condition;
        /**
         * true -> <#if condition>...</#if>
         * <p>
         * false -> <#if !condition>...</#if>
         */
        private Boolean boolVal;
        private String checkType = CodeSnippetCheckTypeEnum.EQUALS.getValue();
    }
}
