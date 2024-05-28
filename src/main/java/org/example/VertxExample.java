package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
/*
 * navigate to http://localhost:8081
 */
public class VertxExample extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new VertxExample());
    }

    @Override
    public void start() {
        vertx.createHttpServer().requestHandler(req -> {
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello, Vert.x!");
        }).listen(8081);
    }
}
