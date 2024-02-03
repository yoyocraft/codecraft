package com.youyi.craft.model.vo;

import cn.hutool.json.JSONUtil;
import com.youyi.craft.meta.Meta;
import com.youyi.craft.model.entity.Generator;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 生成器视图
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class GeneratorVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 基础包
     */
    private String basePackage;

    /**
     * 版本
     */
    private String version;

    /**
     * 作者
     */
    private String author;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 图片
     */
    private String picture;

    /**
     * 文件配置（json字符串）
     */
    private Meta.FileConfig fileConfig;

    /**
     * 模型配置（json字符串）
     */
    private Meta.ModelConfig modelConfig;

    /**
     * 代码生成器产物路径
     */
    private String distPath;

    /**
     * 状态：0-默认
     */
    private Integer status;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户
     */
    private UserVO user;

    /**
     * 包装类转对象
     */
    public static Generator voToObj(GeneratorVO generatorVO) {
        if (Objects.isNull(generatorVO)) {
            return null;
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorVO, generator);
        List<String> tagList = generatorVO.getTags();
        generator.setTags(JSONUtil.toJsonStr(tagList));
        Meta.FileConfig fileConfig = generatorVO.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorVO.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        return generator;
    }

    /**
     * 对象转包装类
     */
    public static GeneratorVO objToVo(Generator generator) {
        if (Objects.isNull(generator)) {
            return null;
        }
        GeneratorVO generatorVO = new GeneratorVO();
        BeanUtils.copyProperties(generator, generatorVO);
        generatorVO.setTags(JSONUtil.toList(generator.getTags(), String.class));
        generatorVO.setFileConfig(
                JSONUtil.toBean(generator.getFileConfig(), Meta.FileConfig.class));
        generatorVO.setModelConfig(
                JSONUtil.toBean(generator.getModelConfig(), Meta.ModelConfig.class));
        return generatorVO;
    }
}
