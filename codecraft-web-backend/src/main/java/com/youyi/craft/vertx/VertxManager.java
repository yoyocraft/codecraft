package com.youyi.craft.vertx;

import com.youyi.craft.manager.CacheManager;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class VertxManager {

    @Resource
    private CacheManager cacheManager;

    //    @PostConstruct
    public void init() {
        Vertx vertx = Vertx.vertx();
        Verticle myVerticle = new MainVerticle(cacheManager);
        vertx.deployVerticle(myVerticle);
    }

}
