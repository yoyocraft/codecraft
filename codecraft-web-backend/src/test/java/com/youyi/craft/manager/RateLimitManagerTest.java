package com.youyi.craft.manager;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
class RateLimitManagerTest {

    RateLimiter limiter = RateLimiter.create(2);

    @Test
    void tryAcquire() throws InterruptedException {
        // 开启十个线程，同时申请令牌
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true) {
                    if (limiter.tryAcquire()) {
                        log.info("获取到令牌");
                        break;
                    }
                }
            }).start();
        }
        // 等待执行完成
        Thread.sleep(10000);
    }
}