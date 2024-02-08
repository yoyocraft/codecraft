package com.youyi.craft.manager;


import java.util.Arrays;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@SpringBootTest
class CosManagerTest {

    @Resource
    private CosManager cosManager;

    @Test
    void deleteObject() {
        cosManager.deleteObject("/generator_dist/1/F2tW4FEo-test.pdf");
    }

    @Test
    void deleteObjects() {
        cosManager.deleteObjects(
                Arrays.asList("generator_dist/1/star.jpeg", "generator_dist/1/npe.png"));
    }

    @Test
    void deleteDir() {
        cosManager.deleteDir("/test/");
    }
}