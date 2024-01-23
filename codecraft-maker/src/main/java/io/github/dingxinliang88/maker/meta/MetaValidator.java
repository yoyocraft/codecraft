package io.github.dingxinliang88.maker.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import io.github.dingxinliang88.maker.meta.enums.FileGenerateTypeEnum;
import io.github.dingxinliang88.maker.meta.enums.FileTypeEnum;
import io.github.dingxinliang88.maker.meta.enums.ModelTypeEnum;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;

/**
 * 元信息校验
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MetaValidator {

    public static void doValidateAndFill(Meta meta) {
        validateAndFillBasic(meta);
        validateAndFillFile(meta);
        validateAndFillModel(meta);
    }

    private static void validateAndFillModel(Meta meta) {
        // modelConfig 校验和默认值
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        List<Meta.ModelConfig.ModelInfo> modelInfoList = modelConfig.getModels();
        if (CollUtil.isEmpty(modelInfoList)) {
            return;
        }
        for (Meta.ModelConfig.ModelInfo modelInfo : modelInfoList) {
            // 输出字段必填
            String fieldName = modelInfo.getFieldName();
            if (StrUtil.isBlank(fieldName)) {
                throw new MetaException("fieldName is required");
            }

            String modelInfoType = modelInfo.getType();
            if (StrUtil.isEmpty(modelInfoType)) {
                modelInfo.setType(ModelTypeEnum.STRING.getValue());
            }
        }
    }

    private static void validateAndFillFile(Meta meta) {
        // fileConfig 校验和默认值
        Meta.FileConfig fileConfig = meta.getFileConfig();
        if (fileConfig == null) {
            return;
        }
        // srcRootPath: 必填
        String srcRootPath = fileConfig.getSrcRootPath();
        if (StrUtil.isBlank(srcRootPath)) {
            throw new MetaException("srcRootPath is required");
        }

        // src: 默认为 .source + srcRootPath 的最后一个层级的路径
        String src = fileConfig.getSrc();
        String defaultSrc = ".source" + File.separator + FileUtil.getLastPathEle(Paths.get(src))
                .getFileName().toString();
        if (StrUtil.isEmpty(src)) {
            fileConfig.setSrc(defaultSrc);
        }

        // dest： 默认为当前路径下的 generated
        String dest = fileConfig.getDest();
        String defaultDest = "generated";
        if (StrUtil.isEmpty(dest)) {
            fileConfig.setDest(defaultDest);
        }

        // type: 默认为 dir
        String type = fileConfig.getType();
        if (StrUtil.isEmpty(type)) {
            fileConfig.setType(FileTypeEnum.DIR.getValue());
        }

        List<Meta.FileConfig.FileInfo> fileInfoList = fileConfig.getFiles();
        if (CollUtil.isEmpty(fileInfoList)) {
            return;
        }
        for (Meta.FileConfig.FileInfo fileInfo : fileInfoList) {
            // src: 必填
            String srcInner = fileInfo.getSrc();
            if (StrUtil.isBlank(srcInner)) {
                throw new MetaException("src is required");
            }

            // dest: 默认等于 src
            String destInner = fileInfo.getDest();
            if (StrUtil.isEmpty(destInner)) {
                fileInfo.setDest(srcInner);
            }

            // type: 默认 src 有文件后缀（如 .java）为 file, 否则为 dir
            String typeInner = fileInfo.getType();
            if (StrUtil.isBlank(typeInner)) {
                // 无文件后缀
                if (StrUtil.isBlank(FileUtil.getSuffix(srcInner))) {
                    fileInfo.setType(FileTypeEnum.DIR.getValue());
                } else {
                    fileInfo.setType(FileTypeEnum.FILE.getValue());
                }
            }

            // generateType: 如果文件末尾不为 ftl，generateType 默认为 static，否则为 dynamic
            String generateType = fileInfo.getGenerateType();
            if (StrUtil.isBlank(generateType)) {
                // 为动态模板
                if (srcInner.endsWith(".ftl")) {
                    fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
                } else {
                    fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
                }
            }
        }
    }

    private static void validateAndFillBasic(Meta meta) {
        // 基础信息校验和默认值
        String name = StrUtil.blankToDefault(meta.getName(), "my-generator");
        String description = StrUtil.emptyToDefault(meta.getDescription(), "my code generator");
        String author = StrUtil.emptyToDefault(meta.getAuthor(), "youyi");
        String basePackage = StrUtil.blankToDefault(meta.getBasePackage(), "com.youyi");
        String version = StrUtil.emptyToDefault(meta.getVersion(), "1.0.0");
        String createTime = StrUtil.emptyToDefault(meta.getCreateTime(), DateUtil.now());

        meta.setName(name);
        meta.setDescription(description);
        meta.setBasePackage(basePackage);
        meta.setVersion(version);
        meta.setAuthor(author);
        meta.setCreateTime(createTime);
    }
}
