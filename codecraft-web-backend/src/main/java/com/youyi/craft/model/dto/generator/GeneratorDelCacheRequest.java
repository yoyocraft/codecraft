package com.youyi.craft.model.dto.generator;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 删除代码生成器缓存请求
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class GeneratorDelCacheRequest implements Serializable {
    
    /**
     * 生成器 ID
     */
    private List<Long> ids;

    private static final long serialVersionUID = 1L;
}
