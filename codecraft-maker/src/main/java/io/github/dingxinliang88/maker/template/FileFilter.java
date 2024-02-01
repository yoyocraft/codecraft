package io.github.dingxinliang88.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import io.github.dingxinliang88.maker.template.enums.FileFilterRangeEnum;
import io.github.dingxinliang88.maker.template.enums.FileFilterRuleEnum;
import io.github.dingxinliang88.maker.template.model.FileFilterConfig;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文件过滤器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class FileFilter {

    /**
     * 对指定文件路径下的文件进行过滤处理
     *
     * @param filePath 文件路径
     * @param filterConfigList 文件过滤配置列表
     * @return 过滤后的文件列表
     */
    public static List<File> doFileFilter(String filePath, List<FileFilterConfig> filterConfigList) {
        // 获取路径下的所有文件
        List<File> fileList = FileUtil.loopFiles(filePath);
        return fileList.stream()
                .filter(file -> doSingleFileFilter(file, filterConfigList))
                .collect(Collectors.toList());
    }

    /**
     * 对单个文件进行过滤。
     *
     * @param file             文件对象
     * @param filterConfigList 过滤配置列表
     * @return 过滤结果，true 表示通过过滤，false 表示不通过过滤
     */
    public static boolean doSingleFileFilter(File file, List<FileFilterConfig> filterConfigList) {
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);

        boolean filterRes = true;

        if (CollUtil.isEmpty(filterConfigList)) {
            return filterRes;
        }

        for (FileFilterConfig filterConfig : filterConfigList) {
            String range = filterConfig.getRange();
            String rule = filterConfig.getRule();
            String value = filterConfig.getValue();

            FileFilterRangeEnum rangeEnum = FileFilterRangeEnum.getEnumByValue(range);
            if (Objects.isNull(rangeEnum)) {
                continue;
            }
            // 过滤内容，默认过滤文件名
            String filterContent = fileName;
            switch (rangeEnum) {
                case FILE_NAME:
                    filterContent = fileName;
                    break;
                case FILE_CONTENT:
                    filterContent = fileContent;
                    break;
                default:
            }

            // 过滤类别
            FileFilterRuleEnum ruleEnum = FileFilterRuleEnum.getEnumByValue(rule);
            if (Objects.isNull(ruleEnum)) {
                continue;
            }
            switch (ruleEnum) {
                case CONTAINS:
                    filterRes = filterContent.contains(value);
                    break;
                case START_WITH:
                    filterRes = filterContent.startsWith(value);
                    break;
                case END_WITH:
                    filterRes = filterContent.endsWith(value);
                    break;
                case REGEX:
                    filterRes = filterContent.matches(value);
                    break;
                case EQUALS:
                    filterRes = filterContent.equals(value);
                    break;
                default:
            }

            if (!filterRes) {
                return false;
            }
        }
        return filterRes;
    }


}
