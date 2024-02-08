package com.youyi.craft.mapper;

import com.youyi.craft.model.entity.Generator;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Select;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public interface GeneratorMapper extends BaseMapper<Generator> {

    /**
     * 查询所有删除了的 generator
     *
     * @return
     */
    @Select("SELECT id, distPath FROM generator WHERE isDelete = 1;")
    List<Generator> listDeletedGenerator();

}




