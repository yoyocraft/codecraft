package io.github.dingxinliang88.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.github.dingxinliang88.maker.meta.Meta;
import io.github.dingxinliang88.maker.meta.enums.FileGenerateTypeEnum;
import io.github.dingxinliang88.maker.meta.enums.FileTypeEnum;
import io.github.dingxinliang88.maker.template.enums.CodeSnippetCheckTypeEnum;
import io.github.dingxinliang88.maker.template.model.TemplateMakerConfig;
import io.github.dingxinliang88.maker.template.model.TemplateMakerFileConfig;
import io.github.dingxinliang88.maker.template.model.TemplateMakerModelConfig;
import io.github.dingxinliang88.maker.template.model.TemplateMakerOutputConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 模板制作
 * <p>
 * 生成器通用能力：
 * <p>
 * 1. 同参数控制多个文件生成 2. 参数控制是否输入某一组配置 3. 参数控制文件和代码的生成
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class TemplateMaker {

    /**
     * 根据提供的模板制作者配置创建模板。
     *
     * @param templateMakerConfig 模板制作者配置对象，包含模板制作者所需的参数和配置
     * @return 创建的模板的ID
     */
    public static Long makeTemplate(TemplateMakerConfig templateMakerConfig) {
        Long id = templateMakerConfig.getId();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        Meta meta = templateMakerConfig.getMeta();
        TemplateMakerFileConfig fileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig modelConfig = templateMakerConfig.getModelConfig();
        TemplateMakerOutputConfig outputConfig = templateMakerConfig.getOutputConfig();
        return makeTemplate(meta, originProjectPath, fileConfig, modelConfig,
                outputConfig, id);
    }

    /**
     * 生成模板文件。
     *
     * @param newMeta                   新的元信息对象
     * @param originProjectPath         原始项目路径
     * @param templateMakerFileConfig   模板制作文件配置
     * @param templateMakerModelConfig  模板制作模型配置
     * @param templateMakerOutputConfig 模板制作输出配置
     * @param id                        模板文件的唯一标识
     * @return 模板文件的唯一标识
     */
    public static Long makeTemplate(Meta newMeta, String originProjectPath,
            TemplateMakerFileConfig templateMakerFileConfig,
            TemplateMakerModelConfig templateMakerModelConfig,
            TemplateMakerOutputConfig templateMakerOutputConfig, Long id) {

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
        // 1.1 文件信息，获取项目根目录 sourceRootPath
        String sourceRootPath = FileUtil.loopFiles(new File(templatePath), 1, null)
                .stream()
                .filter(File::isDirectory)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("未找到模板文件"))
                .getAbsolutePath();
        // 兼容 win
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");

        // 2. 生成文件模板
        List<Meta.FileConfig.FileInfo> newFileInfoList = makeFileTemplates(
                templateMakerFileConfig, templateMakerModelConfig, sourceRootPath);
        // 处理模型信息
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = getModelInfoList(
                templateMakerModelConfig);

        // 3. 生成 meta.json 文件，和 template 同级
        String metaOutputPath = templatePath + File.separator + "meta.json";

        // 如果已有 meta.json 文件，说明不是第一次制作，则在 meta.json 的基础上进行修改
        if (FileUtil.exist(metaOutputPath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            // 1. 追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);

            // 2. 配置信息去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
        }
        // 第一次制作
        else {
            // 1. 构造配置参数
            // 文件配置
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            // 模型配置
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);

            // 2. 设置模型参数
            fileInfoList.addAll(newFileInfoList);
            modelInfoList.addAll(newModelInfoList);
        }

        // 2. 额外的输出配置
        if (Objects.nonNull(templateMakerOutputConfig)) {
            // 文件外层和分组去重
            if (templateMakerOutputConfig.isRemoveGroupFilesFromRoot()) {
                List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
                newMeta.getFileConfig()
                        .setFiles(TemplateMakerUtil.removeGroupFilesFromRoot(fileInfoList));
            }
        }

        // 3. 输出 meta.json 元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);

        return id;
    }

    /**
     * 获取模型信息列表
     *
     * @param templateMakerModelConfig 模型配置
     * @return 模型信息列表
     */
    private static List<Meta.ModelConfig.ModelInfo> getModelInfoList(
            TemplateMakerModelConfig templateMakerModelConfig) {
        // 当前轮次新增的模型配置列表
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();
        // npe validate
        if (Objects.isNull(templateMakerModelConfig)) {
            return newModelInfoList;
        }
        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        if (CollUtil.isEmpty(models)) {
            return newModelInfoList;
        }
        // 转换为可以接收的 ModelInfo 对象
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
                    BeanUtil.copyProperties(modelInfoConfig, modelInfo);
                    return modelInfo;
                })
                .collect(Collectors.toList());

        // 如果是模型组
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (Objects.nonNull(modelGroupConfig)) {
            Meta.ModelConfig.ModelInfo groupModelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelGroupConfig, groupModelInfo);

            // 模型放到一个分组下
            groupModelInfo.setModels(inputModelInfoList);
            newModelInfoList.add(groupModelInfo);
        } else {
            // 不分组，添加所有的模型信息
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }

    /**
     * 根据提供的模板制作者文件配置和模型配置创建文件模板。
     *
     * @param templateMakerFileConfig  模板制作者文件配置对象，包含文件配置和过滤器配置
     * @param templateMakerModelConfig 模板制作者模型配置对象，包含模型配置
     * @param sourceRootPath           原始文件路径的根路径
     * @return 创建的文件模板列表
     */
    private static List<Meta.FileConfig.FileInfo> makeFileTemplates(
            TemplateMakerFileConfig templateMakerFileConfig,
            TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        // npe validation
        if (Objects.isNull(templateMakerFileConfig)) {
            return newFileInfoList;
        }

        List<TemplateMakerFileConfig.FileInfoConfig> fileConfigInfoList = templateMakerFileConfig.getFiles();
        if (CollUtil.isEmpty(fileConfigInfoList)) {
            return newFileInfoList;
        }
        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileConfigInfoList) {
            String inputFilePath = fileInfoConfig.getPath();

            // 如果填的是相对路径，改成绝对路径
            if (!inputFilePath.startsWith(sourceRootPath)) {
                inputFilePath = sourceRootPath + File.separator + inputFilePath;
            }

            // 获取过滤后的文件列表（不存在目录）
            List<File> fileList = FileFilter.doFileFilter(inputFilePath,
                    fileInfoConfig.getFilterConfigList());
            // 不处理已生成的 ftl 模板文件
            fileList = fileList.stream()
                    .filter(file -> !file.getAbsolutePath().endsWith(".ftl"))
                    .toList();
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(file, fileInfoConfig,
                        templateMakerModelConfig, sourceRootPath);
                newFileInfoList.add(fileInfo);
            }
        }

        // 如果是文件组
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if (Objects.nonNull(fileGroupConfig)) {
            // 新增分组配置
            Meta.FileConfig.FileInfo groupFileInfo = new Meta.FileConfig.FileInfo();
            groupFileInfo.setType(FileTypeEnum.GROUP.getValue());
            groupFileInfo.setCondition(fileGroupConfig.getCondition());
            groupFileInfo.setGroupKey(fileGroupConfig.getGroupKey());
            groupFileInfo.setGroupName(fileGroupConfig.getGroupName());
            groupFileInfo.setFiles(newFileInfoList);

            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(groupFileInfo);
        }
        return newFileInfoList;
    }

    /**
     * 生成文件模板。
     *
     * @param inputFile                输入文件
     * @param templateMakerModelConfig 模型配置
     * @param sourceRootPath           源路径
     * @return 文件配置信息对象
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(File inputFile,
            TemplateMakerFileConfig.FileInfoConfig fileInfoConfig,
            TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        // 1. 路径转换
        // 绝对路径
        String fileInputAbsolutePath = inputFile.getAbsolutePath()
                .replaceAll("\\\\", "/"); // 兼容 win
        String fileOutputAbsolutePath = fileInputAbsolutePath + ".ftl";

        // 相对路径
        String fileInputPath = fileInputAbsolutePath.replace(sourceRootPath + "/", "");
        String fileOutputPath = fileInputPath + ".ftl";

        // 2. 使用字符串替换算法，生成 ftl 模板文件
        String fileContent;
        boolean hasTemplateFile = FileUtil.exist(fileOutputAbsolutePath);
        if (hasTemplateFile) {
            // 如果已有模板文件，说明不是第一次制作，需要在模板的基础上再次制作
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        // 支持多个模型：对同一个文件内容，遍历模型进行多轮替换
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        String newFileContent = fileContent;
        String replacement;
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            if (Objects.isNull(modelGroupConfig)) {
                // 不是分组
                replacement = String.format("${%s}", modelInfoConfig.getFieldName());
            } else {
                // 是分组
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s.%s}", groupKey,
                        modelInfoConfig.getFieldName()); // 多一个层级
            }
            // 多次替换
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(),
                    replacement);
        }

        // 文件路径替换
        TemplateMakerModelConfig.ModelInfoConfig fileDirPathConfig = templateMakerModelConfig.getFileDirPathConfig();
        if (Objects.nonNull(fileDirPathConfig)) {
            String[] inputPathAndFileSuffix = fileInputPath.split("\\.");
            if (inputPathAndFileSuffix.length > 1) {
                String fileSuffix = inputPathAndFileSuffix[1];
                // 拿到要替换的包名，e.g: com.youyi => com/youyi
                String replaceText = fileDirPathConfig.getReplaceText().replace(".", "/");
                // 替换路径，e.g: src/main/java/com/youyi/project/Demo.java => src/main/java/{basePackage}/project/Demo.java
                fileInputPath = inputPathAndFileSuffix[0].replace(replaceText,
                        String.format("{%s}", fileDirPathConfig.getFieldName()));
                // 拼接后缀
                fileInputPath = fileInputPath + "." + fileSuffix;
            }
        }

        // 控制代码片段是否生成
        List<TemplateMakerFileConfig.CodeSnippetConfig> codeSnippetConfigList = fileInfoConfig.getCodeSnippetConfigList();
        if (CollUtil.isNotEmpty(codeSnippetConfigList)) {
            for (TemplateMakerFileConfig.CodeSnippetConfig codeSnippetConfig : codeSnippetConfigList) {
                String code = codeSnippetConfig.getCode();
                String condition = codeSnippetConfig.getCondition();
                boolean boolVal = codeSnippetConfig.getBoolVal();
                String checkType = codeSnippetConfig.getCheckType();
                CodeSnippetCheckTypeEnum checkTypeEnum = CodeSnippetCheckTypeEnum.getEnumByValue(
                        checkType);
                if (Objects.isNull(checkTypeEnum)) {
                    continue;
                }
                String replaceCodeSnippets = null;
                switch (checkTypeEnum) {
                    case EQUALS -> replaceCodeSnippets = String.format("\n<#if %s>\n%s\n</#if>\n",
                            boolVal ? condition : "!" + condition, code);
                    case REGEX -> {
                        // 根据正则找到需要匹配的内容
                        code = ReUtil.get(code, fileContent, 0);
                        replaceCodeSnippets = String.format("\n<#if %s>\n%s\n</#if>\n",
                                boolVal ? condition : "!" + condition, code);
                    }
                    default -> {
                    }
                }
                // 判断目前内容中是否已有我们需要生成的代码片段，如果存在，说明已经加工过了，不需要操作，否则执行替换操作
                boolean contains = StrUtil.contains(newFileContent, replaceCodeSnippets);
                if (!contains) {
                    newFileContent = StrUtil.replace(newFileContent, code,
                            replaceCodeSnippets);
                }
            }
        }

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileOutputPath); // 文件输入路径是 ftl 文件
        fileInfo.setOutputPath(fileInputPath);
        fileInfo.setCondition(fileInfoConfig.getCondition());
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        boolean contentEquals = newFileContent.equals(fileContent);
        // 不存在模板文件 && 没有更改文件内容 => 静态生成
        if (!hasTemplateFile) {
            if (contentEquals) {
                // 输入路径 = 输出路径
                fileInfo.setInputPath(fileInputPath);
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            } else {
                // 没有模板文件，输出模板文件
                FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);
            }
        } else if (!contentEquals) {
            // 有模板文件，并且文件内容发生改变，生成新的模板文件
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

        // 1. 将所有文件配置 FileInfo 分为有分组和无分组的，先过滤出有分组的进行处理
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoListMap = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey)
                );

        // 2. 对于有分组的文件配置，如果有相同的分组，同分组内的文件进行合并，不同分组可同时保留
        Map<String, Meta.FileConfig.FileInfo> groupKeyMergedFileInfoMap = new HashMap<>(); // 保存每个组对应的合并后的对象 map
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoListMap.entrySet()) {
            List<Meta.FileConfig.FileInfo> tmpFileInfoList = entry.getValue();
            // 按照 outputPath 进行去重，inputPath 是 ftl 文件
            List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(
                    tmpFileInfoList.stream()
                            .flatMap(fileInfo -> fileInfo.getFiles().stream())
                            .collect(
                                    Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath,
                                            o -> o, (e, r) -> r)
                            ).values()
            );
            // 使用新的 group 配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tmpFileInfoList); // 最后一个元素是最新的
            newFileInfo.setFiles(newFileInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedFileInfoMap.put(groupKey, newFileInfo);
        }

        // 3. 创建新的文件配置列表，将合并后的分组添加到列表
        List<Meta.FileConfig.FileInfo> resultFileInfoList = new ArrayList<>(
                groupKeyMergedFileInfoMap.values());

        // 4. 将无分组的文件配置添加的列表
        List<Meta.FileConfig.FileInfo> noGroupFileInfoList = fileInfoList
                .stream()
                .filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                .toList();
        resultFileInfoList.addAll(new ArrayList<>(noGroupFileInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, o -> o,
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
        // 针对分组的策略：相同分组下的模型 merge，不同分组保留

        // 1. 将所有模型配置 ModelInfo 分为有分组和无分组的，先处理有分组下的模型信息
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoListMap = modelInfoList
                .stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(
                        Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey)
                );

        // 2. 对于有分组的模型配置，如果有相同的分组，同分组内的模型进行合并，不同分组可同时保留
        Map<String, Meta.ModelConfig.ModelInfo> groupKeyMergedModelInfoMap = new HashMap<>(); // 保存每个组对应的合并后的对象 map
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfoListMap.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tmpModelInfoList = entry.getValue();
            // 按照 fieldName 进行去重
            List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(
                    tmpModelInfoList.stream()
                            .flatMap(modelInfo -> modelInfo.getModels().stream())
                            .collect(
                                    Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName,
                                            o -> o, (e, r) -> r)
                            ).values()
            );
            // 使用新的 group 配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(
                    tmpModelInfoList); // 最后一个元素是最新的
            newModelInfo.setModels(newModelInfoList);
            String groupKey = entry.getKey();
            groupKeyMergedModelInfoMap.put(groupKey, newModelInfo);
        }

        // 3. 创建新的模型配置列表，将合并后的分组添加到列表
        List<Meta.ModelConfig.ModelInfo> resultModelInfoList = new ArrayList<>(
                groupKeyMergedModelInfoMap.values());

        // 4. 将无分组的模型配置添加的列表
        List<Meta.ModelConfig.ModelInfo> noGroupModelInfoList = modelInfoList
                .stream()
                .filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                .toList();
        resultModelInfoList.addAll(new ArrayList<>(noGroupModelInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, o -> o,
                                (e, r) -> r)
                ).values()));
        return resultModelInfoList;
    }

}
