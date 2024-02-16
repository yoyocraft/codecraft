package com.youyi.craft.manager;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.util.concurrent.RateLimiter;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
class RateLimitManagerTest {

    RateLimiter limiter = RateLimiter.create(2);

    @Test
    void tryAcquire() throws InterruptedException {
        // 开启十个线程，同时申请令牌
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                while (true) {
                    if (limiter.tryAcquire()) {
                        System.out.println("获取到令牌");
                        break;
                    }
                }
            }).start();
        }
        // 等待执行完成
        Thread.sleep(10000);
    }
}