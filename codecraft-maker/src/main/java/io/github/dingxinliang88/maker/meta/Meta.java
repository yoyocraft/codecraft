package io.github.dingxinliang88.maker.meta;

import java.util.List;

/**
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
    private FileConfig fileConfig;
    private ModelConfig modelConfig;
    private Boolean versionControl;

    @lombok.NoArgsConstructor
    @lombok.Data
    public static class FileConfig {

        private String srcRootPath;
        private String src;
        private String dest;
        private String type;
        private List<FileInfo> files;

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class FileInfo {

            private String src;
            private String dest;
            private String type;
            private String generateType;
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
        }
    }
}
