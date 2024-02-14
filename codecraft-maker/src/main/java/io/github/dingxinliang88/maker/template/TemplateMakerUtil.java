package io.github.dingxinliang88.maker.template;

import cn.hutool.core.util.StrUtil;
import io.github.dingxinliang88.maker.meta.Meta;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模板制作工具类
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class TemplateMakerUtil {


    /**
     * 从未分组中移除组内同名文件
     *
     * @param fileInfoList 文件列表
     * @return 移除后的文件列表
     */
    public static List<Meta.FileConfig.FileInfo> removeGroupFilesFromRoot(
            List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 1. 获取所有分组
        List<Meta.FileConfig.FileInfo> groupFileInfoList = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .toList();

        // 2. 获取所有分组内的文件列表
        List<Meta.FileConfig.FileInfo> groupInnerFileInfoList = groupFileInfoList.stream()
                .flatMap(fileInfo -> fileInfo.getFiles().stream())
                .toList();

        // 3. 获取所有分组内文件的输入路径集合
        Set<String> fileInputPathSet = groupInnerFileInfoList.stream()
                .map(Meta.FileConfig.FileInfo::getInputPath)
                .collect(Collectors.toSet());

        // 4. 移除所有名称在 set 中的外层文件
        return fileInfoList.stream()
                .filter(fileInfo -> !fileInputPathSet.contains(fileInfo.getInputPath()))
                .collect(Collectors.toList());
    }
}
