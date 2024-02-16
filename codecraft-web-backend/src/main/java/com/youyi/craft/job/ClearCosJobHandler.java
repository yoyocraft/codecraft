package com.youyi.craft.job;

import cn.hutool.core.util.StrUtil;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.youyi.craft.manager.CosManager;
import com.youyi.craft.mapper.GeneratorMapper;
import com.youyi.craft.model.entity.Generator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Component
@Slf4j
public class ClearCosJobHandler {

    @Resource
    private CosManager cosManager;

    @Resource
    private GeneratorMapper generatorMapper;

    @XxlJob("clearCosJobHandler")
    public void clearCosJobHandler() {
        log.info("clear cos job handler start...");

        // 1. 包括用户上传的模板制作文件（`generator_make_template`）
        // TODO 思考问题：定时批量删除模板制作文件会不会有什么问题？比如刚上传就删了。
        cosManager.deleteDir("/generator_make_template/");
        // 2. 已删除的代码生成器对应的产物包文件（`generator_dist`）
        List<Generator> generatorList = generatorMapper.listDeletedGenerator();
        List<String> keyList = generatorList.stream().map(Generator::getDistPath)
                .filter(StrUtil::isNotBlank)
                // 去除 / 前缀
                .map(path -> path.substring(1))
                .collect(Collectors.toList());
        cosManager.deleteObjects(keyList);
        log.info("clear cos job handler stop...");
    }

}
