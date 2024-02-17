package com.youyi.craft.mapper;

import com.youyi.craft.constant.GeneratorConstant;
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


    /**
     * 查询 hot generator
     * <p>
     * 只查询 TOP 10，其余的在使用之后判断
     *
     * @see GeneratorConstant#HOT_GENERATOR_USE_COUNT_THRESHOLD
     */
    @Select("SELECT id FROM generator WHERE useCount >= 1000 AND isDelete = 0 ORDER BY useCount DESC LIMIT 10;")
    List<Long> listHotGeneratorIds();
}




