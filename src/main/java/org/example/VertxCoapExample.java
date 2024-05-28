package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class VertxCoapExample extends AbstractVerticle {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new VertxCoapExample());
    }

    @Override
    public void start() {
        CoapServer server = new CoapServer();
        server.add(new CoapResource("hello") {
            @Override
            public void handleGET(CoapExchange exchange) {
                exchange.respond("Hello, CoAP from Vert.x!");
            }
        });
        server.start();
    }
}
