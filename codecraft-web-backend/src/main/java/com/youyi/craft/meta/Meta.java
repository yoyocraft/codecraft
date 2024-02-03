package com.youyi.craft.meta;

import java.util.List;

/**
 * meta 元信息
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@lombok.NoArgsConstructor
@lombok.Data
public class Meta {

    private String name;
    private String description;
    private String basePackage;
    private String version;
    private String author;
    private String createTime;
    private Boolean versionControl;
    private FileConfig fileConfig;
    private ModelConfig modelConfig;

    @lombok.NoArgsConstructor
    @lombok.Data
    public static class FileConfig {

        private String sourceRootPath;
        private String inputRootPath;
        private String outputRootPath;
        private String type;
        private List<FileInfo> files;

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class FileInfo {

            private String inputPath;
            private String outputPath;
            private String type;
            private String generateType;
            private String condition;
            // group config
            private String groupKey;
            private String groupName;
            private List<FileInfo> files;

        }
    }

    @lombok.NoArgsConstructor
    @lombok.Data
    public static class ModelConfig {

        private List<ModelInfo> models;

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class ModelInfo {

            private String fieldName;
            private String type;
            private String description;
            private Object defaultValue;
            private String abbr;
            private String condition;
            // group config
            private String groupKey;
            private String groupName;
            private List<ModelInfo> models;

            // tmp args, not for command use
            private String allArgsStr;
        }
    }
}
