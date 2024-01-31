package io.github.dingxinliang88.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.github.dingxinliang88.maker.meta.Meta;
import io.github.dingxinliang88.maker.meta.enums.FileGenerateTypeEnum;
import io.github.dingxinliang88.maker.meta.enums.FileTypeEnum;
import io.github.dingxinliang88.maker.meta.enums.ModelTypeEnum;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
                new File(projectPath).getParent() + File.separator + "sample/acm-template";
        String inputFilePath = "src/io/github/dingxinliang88/acm/MainTemplate.java";

        // 模型参数信息（首次）
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("outputText");
        modelInfo.setType(ModelTypeEnum.STRING.getValue());
        modelInfo.setDefaultValue("sum = ");
        // 替换变量（首次）
        String searchStr = "Sum: ";

        // 模型参数信息（第二次）
//        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("className");
//        modelInfo.setDescription("类名");
//        modelInfo.setType(ModelTypeEnum.STRING.getValue());
        // 替换变量（第二次）
//        String searchStr = "MainTemplate";

        long id = makeTemplate(meta, originProjectPath, inputFilePath, modelInfo, searchStr, 1L);
        System.out.println("id = " + id);
    }

    /**
     * 生成模板文件。
     *
     * @param newMeta           新的元信息对象
     * @param originProjectPath 原始项目路径
     * @param inputFilePath     输入文件路径
     * @param modelInfo         模型信息对象
     * @param searchStr         搜索字符串
     * @param id                模板文件的唯一标识
     * @return 模板文件的唯一标识
     */
    private static Long makeTemplate(Meta newMeta, String originProjectPath,
            final String inputFilePath,
            Meta.ModelConfig.ModelInfo modelInfo, String searchStr, Long id) {

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

        String fileInputPath = inputFilePath;
        String fileOutputPath = fileInputPath + ".ftl";

        // 2. 使用字符串替换算法，生成 ftl 模板文件
        String fileInputAbsolutePath = sourceRootPath + File.separator + fileInputPath;
        String fileOutputAbsolutePath = sourceRootPath + File.separator + fileOutputPath;

        String fileContent;
        // 如果已有模板文件，说明不是第一次制作，需要在模板的基础上再次制作
        if (FileUtil.exist(fileOutputAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);

        // 输出模板文件
        FileUtil.writeUtf8String(newFileContent, fileOutputAbsolutePath);

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 3. 生成 meta.json 文件，和 template 同级
        String metaOutputPath = templatePath + File.separator + "meta.json";

        // 如果已有 meta.json 文件，说明不是第一次制作，则在 meta.json 的基础上进行修改
        if (FileUtil.exist(metaOutputPath)) {
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);
            BeanUtil.copyProperties(newMeta, oldMeta, CopyOptions.create().ignoreNullValue());
            newMeta = oldMeta;

            // 1. 追加配置参数
            List<Meta.FileConfig.FileInfo> fileInfoList = oldMeta.getFileConfig().getFiles();
            fileInfoList.add(fileInfo);
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
            fileInfoList.add(fileInfo);

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
     * 去除重复的文件信息，只保留唯一的文件路径。
     *
     * @param fileInfoList 文件信息列表
     * @return 去除重复文件信息后的列表
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(
            List<Meta.FileConfig.FileInfo> fileInfoList) {
        return new ArrayList<>(fileInfoList.stream()
                .collect(
                        Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, o -> o,
                                (e, r) -> r) // 使用新元素替换旧元素
                ).values()
        );
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
