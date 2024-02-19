package com.youyi.craft.model.dto.generator;

import io.github.dingxinliang88.maker.meta.Meta;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 制作代码生成器请求
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class GeneratorMakeRequest implements Serializable {


    /**
     * 压缩文件路径
     */
    private String zipFilePath;

    /**
     * 元信息
     */
    private Meta meta;

    /**
     * 文件信息
     */
    private MultipartFile multipartFile;

    @Serial
    private static final long serialVersionUID = 1L;
}
