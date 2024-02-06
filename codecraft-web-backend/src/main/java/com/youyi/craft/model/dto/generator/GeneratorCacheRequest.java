package com.youyi.craft.model.dto.generator;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;

/**
 * 缓存代码生成器请求
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class GeneratorCacheRequest implements Serializable {


    /**
     * 生成器 ID
     */
    private Long id;

    private static final long serialVersionUID = 1L;
}
