package com.youyi.craft.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youyi.craft.model.dto.generator.GeneratorQueryRequest;
import com.youyi.craft.model.entity.Generator;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youyi.craft.model.vo.GeneratorVO;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public interface GeneratorService extends IService<Generator> {

    /**
     * 校验
     *
     * @param generator
     * @param add
     */
    void validGenerator(Generator generator, boolean add);

    /**
     * 获取查询条件
     *
     * @param generatorQueryRequest
     * @return
     */
    QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest);

    /**
     * 获取生成器封装
     *
     * @param generator
     * @param request
     * @return
     */
    GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request);

    /**
     * 分页获取生成器封装
     *
     * @param generatorPage
     * @param request
     * @return
     */
    Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request);

    /**
     * 批量获取
     *
     * @param idList
     * @return
     */
    List<Generator> getBatchByIds(List<Long> idList);
}
