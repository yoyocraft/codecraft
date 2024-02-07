package com.youyi.craft.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class HealthVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        // Create the HTTP server
        vertx.createHttpServer()
                // Handle every request using the router
                .requestHandler(req -> {
                    req.response()
                            .putHeader("Content-Type", "text/plain;charset=UTF-8")
                            .end("ok");
                })
                // Start listening
                .listen(8888)
                // Print the port
                .onSuccess(
                        server -> log.info("HTTP server started on port " + server.actualPort()));
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Verticle myVerticle = new HealthVerticle();
        vertx.deployVerticle(myVerticle);
    }
}

