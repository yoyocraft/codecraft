package io.github.dingxinliang88.maker.template.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class TemplateMakerFileConfig {

    /*
        {
            "files": [
                {
                    "path": "文件（目录）路径",
                    "filters": [
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

        private List<FileFilterConfig> filterConfigList;
    }

    @NoArgsConstructor
    @Data
    public static class FileGroupConfig {

        private String condition;

        private String groupKey;

        private String groupName;
    }
}
