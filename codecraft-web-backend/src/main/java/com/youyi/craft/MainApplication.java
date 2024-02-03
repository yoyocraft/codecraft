package com.youyi.craft;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.DigestUtils;

/**
 * 主类（项目启动入口）
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
@MapperScan("com.youyi.craft.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
