package com.youyi.craft.service;

import com.youyi.craft.model.entity.Generator;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@SpringBootTest
class GeneratorServiceTest {

    @Resource
    private GeneratorService generatorService;

    @Test
    public void insertData() {
        Generator generator = generatorService.getById(12L);
        generator.setId(null);

        // 批量插入
        for (int i = 0; i < 100000; i++) {
            generatorService.save(generator);
            generator.setId(null);
        }

    }

}