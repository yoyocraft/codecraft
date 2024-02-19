package com.youyi.craft.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.youyi.craft.model.dto.generator.GeneratorQueryRequest;
import com.youyi.craft.model.entity.Generator;
import com.youyi.craft.model.vo.GeneratorVO;
import io.github.dingxinliang88.maker.meta.Meta;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

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

    void cacheGenerators(List<Long> idList);

    List<Long> listHotGeneratorIds();

    void downloadGenerator(Generator generator, HttpServletResponse response) throws IOException;

    void onlineUseGenerator(Generator generator, Object dataModel, Long userId,
            HttpServletResponse response) throws IOException;

    void onlineMakerGenerator(Meta meta, String zipFilePath, HttpServletResponse response)
            throws IOException;

    void onlineMakerGenerator(Meta meta, MultipartFile multipartFile, HttpServletResponse response)
            throws IOException;
}
