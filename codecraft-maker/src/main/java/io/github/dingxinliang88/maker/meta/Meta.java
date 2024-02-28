package io.github.dingxinliang88.maker.meta;

import java.io.Serializable;
import java.util.List;

/**
 * 元信息
 * <p>
 * see: doc/meta-example.json
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@lombok.NoArgsConstructor
@lombok.Data
public class Meta implements Serializable {

    private String name;
    private String description;
    private String basePackage;
    private String version;
    private String author;
    private String createTime;
    private FileConfig fileConfig;
    private ModelConfig modelConfig;

    /**
     * 路径选择 折叠路径结构,后续如果有更复杂的情况或者需求变更，可以使用层级递归的形式
     */
    @lombok.NoArgsConstructor
    @lombok.Data
    public static class FileConfig implements Serializable {

        // 源文件绝对路径
        private String sourceRootPath;
        // 拷贝，针对项目相对路径，可移植性优化
        private String inputRootPath;
        private String outputRootPath;
        private String type;
        private List<FileInfo> files;

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class FileInfo implements Serializable {

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
    public static class ModelConfig implements Serializable {

        private List<ModelInfo> models;

        @lombok.NoArgsConstructor
        @lombok.Data
        public static class ModelInfo implements Serializable {

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

            // required
            private Boolean required;

            // tmp args, not for command use
            private String allArgsStr;
        }
    }
}
