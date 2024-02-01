package io.github.dingxinliang88.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.github.dingxinliang88.maker.meta.Meta;
import io.github.dingxinliang88.maker.meta.enums.FileGenerateTypeEnum;
import io.github.dingxinliang88.maker.meta.enums.FileTypeEnum;
import io.github.dingxinliang88.maker.meta.enums.ModelTypeEnum;
import io.github.dingxinliang88.maker.template.enums.FileFilterRangeEnum;
import io.github.dingxinliang88.maker.template.enums.FileFilterRuleEnum;
import io.github.dingxinliang88.maker.template.model.FileFilterConfig;
import io.github.dingxinliang88.maker.template.model.TemplateMakerFileConfig;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 模板制作
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class TemplateMaker {

    public static void main(String[] args) {
        String name = "acm-template-generator";
        String description = "ACM 示例模板生成器";
        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);

        String projectPath = System.getProperty("user.dir");
        String originProjectPath =
                new File(projectPath).getParent() + File.separator + "sample/springboot-init";
        String inputFilePath1 = "src/main/java/com/youyi/springbootinit/common";
        String inputFilePath2 = "src/main/java/com/youyi/springbootinit/constant";

        // 模型参数信息
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("className");
        modelInfo.setType(ModelTypeEnum.STRING.getValue());
        // 替换变量
        String searchStr = "BaseResponse";

        // 文件过滤
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        List<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        fileInfoConfig1.setFilterConfigList(fileFilterConfigList);
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();
        fileFilterConfigList.add(fileFilterConfig);

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(inputFilePath2);
        templateMakerFileConfig.setFiles(Arrays.asList(fileInfoConfig1, fileInfoConfig2));

        // 分组配置
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setCondition("outputText");
        fileGroupConfig.setGroupKey("for test");
        fileGroupConfig.setGroupName("测试分组");
        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);

        long id = makeTemplate(meta, originProjectPath,
                modelInfo, templateMakerFileConfig, searchStr,
                1752657938160234496L);
        System.out.println("id = " + id);
    }

    /**
     * 生成模板文件。
     *
     * @param newMeta                 新的元信息对象
     * @param originProjectPath       原始项目路径
     * @param templateMakerFileConfig 模板制作文件配置
     * @param modelInfo               模型信息对象
     * @param searchStr               搜索字符串
     * @param id                      模板文件的唯一标识
     * @return 模板文件的唯一标识
     */
    private static Long makeTemplate(Meta newMeta, String originProjectPath,
            Meta.ModelConfig.ModelInfo modelInfo, TemplateMakerFileConfig templateMakerFileConfig,
            String searchStr, Long id) {

        if (Objects.isNull(id)) {
            id = IdUtil.getSnowflakeNextId();
        }

        // 0. 工作空间隔离
        String projectPath = System.getProperty("user.dir");
        String tmpDirPath = projectPath + File.separator + ".tmp";
        String templatePath = tmpDirPath + File.separator + id;

        // 首次制作，复制制作模板
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            FileUtil.copy(originProjectPath, templatePath, true);
        }

        // 1. 输入信息
        // 1.1 文件信息
        String sourceRootPath = templatePath + File.separator +
                FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString();
        // 兼容 win
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");

        // 2. 生成文件模板
        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList = templateMakerFileConfig.getFiles();
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileConfigInfoList) {
            String inputFilePath = fileInfoConfig.getPath();

            // 如果填的是相对路径，改成绝对路径
            if (!inputFilePath.startsWith(sourceRootPath)) {
                inputFilePath = sourceRootPath + File.separator + inputFilePath;
            }

            // 获取过滤后的文件列表（不存在目录）
            List<File> fileList = FileFilter.doFileFilter(inputFilePath,
                    fileInfoConfig.getFilterConfigList());
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(file, modelInfo, searchStr,
                        sourceRootPath);
                newFileInfoList.add(fileInfo);
            }
        }

        // 如果是文件组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if (Objects.nonNull(fileGroupConfig)) {
            String condition = fileGroupConfig.getCondition();
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();

            // 新增分组配置
            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            groupFileInfo.setType(FileTypeEnum.GROUP.getValue());
            groupFileInfo.setCondition(condition);
            groupFileInfo.setGroupKey(groupKey);
            groupFileInfo.setGroupName(groupName);
            groupFileInfo.setFiles(newFileInfoList); // 文件全放在一个分组下

            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }

        // 3. 生成 meta.json 文件，和 template 同级
        String metaOutputPath = templatePath + File.separator + "meta.json";

        // 如果已有 meta.json 文件，说明不是第一次制作，则在 meta.json 的基础上进行修改
        if (FileUtil.exist(metaOutputPath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            // 1. 追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfoList = oldMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = oldMeta.getModelConfig().getModels();
            modelInfoList.add(modelInfo);

            // 2. 配置信息去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
        } else {
            // 1. 构造配置参数
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.addAll(newFileInfoList);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.add(modelInfo);
        }

        // 2. 输出 meta.json 元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);

        return id;
    }

    /**
     * 生成文件模板。
     *
     * @param inputFile      输入文件
     * @param modelInfo      模型信息对象
     * @param searchStr      搜索字符串
     * @param sourceRootPath 源路径
     * @return 文件配置信息对象
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(File inputFile,
            Meta.ModelConfig.ModelInfo modelInfo, String searchStr, String sourceRootPath) {
        // 1. 路径转换
        // 绝对路径
        String fileInputAbsolutePath = inputFile.getAbsolutePath()
                .replaceAll("\\\\", "/"); // 兼容 win
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 相对路径
        String fileInputPath = fileInputAbsolutePath.replaceAll(sourceRootPath + "/", "");
        String fileOutputPath = fileInputPath + ".ftl";

        // 2. 使用字符串替换算法，生成 ftl 模板文件
        String fileContent;
        // 如果已有模板文件，说明不是第一次制作，需要在模板的基础上再次制作
        if (FileUtil.exist(fileOutputAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);
        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());

        // 如果和源文件一致，则为静态生成
        if (newFileContent.equals(fileContent)) {
            // 输入路径 = 输出路径
            fileInfo.setOutputPath(fileInputPath);
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
        } else {
            // 生成动态模板
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            // 输出模板文件
            FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
        }

        return fileInfo;
    }


    /**
     * 去除重复的文件信息，只保留唯一的文件路径。
     *
     * @param fileInfoList 文件信息列表
     * @return 去除重复文件信息后的列表
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(
            List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 针对分组的策略：相同分组下的文件 merge，不同分组保留

        // 1. 将所有文件配置 FileInfo 分为有分组和无分组的
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey)
                );

        // 2. 对于有分组的文件配置，如果有相同的分组，同分组内的文件进行合并，不同分组可同时保留
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileINfoMap = new HashMap<>(); // 保存每个组对应的合并后的对象 map
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tmpFileInfoList = entry.getValue();
            // 按照 inputPath 进行去重
            List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(
                    tmpFileInfoList.stream()
                            .flatMap(fileInfo -> fileInfo.getFiles().stream())
                            .collect(
                                    Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o,
                                            (e, r) -> r)
                            ).values()
            );
            // 使用新的 group 配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tmpFileInfoList); // 最后一个元素是最新的
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileINfoMap.put(groupKey, newFileInfo);
        }

        // 3. 创建新的文件配置列表，将合并后的分组添加到列表
        List<Meta.FileConfig.FileInfo> resultFileInfoList = new ArrayList<>(
                groupKeyMergedFileINfoMap.values());

        // 4. 将无分组的文件配置添加的列表
        List<Meta.FileConfig.FileInfo> noGroupFileInfoList = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());
        resultFileInfoList.addAll(new ArrayList<>(noGroupFileInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o,
                                (e, r) -> r)
                ).values()));
        return resultFileInfoList;
    }

    /**
     * 去除重复的模型信息，只保留唯一的字段名。
     *
     * @param modelInfoList 模型信息列表
     * @return 去除重复模型信息后的列表
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(
            List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        return new ArrayList<>(modelInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o,
                                (e, r) -> r) // 使用新元素替换旧元素
                ).values()
        );
    }


}
