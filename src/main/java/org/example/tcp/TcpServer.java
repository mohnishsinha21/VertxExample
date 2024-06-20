package org.example.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge;

public class TcpServer {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        // Configure the EventBus bridge options
        BridgeOptions bridgeOptions = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("inbound.address"))
                .addOutboundPermitted(new PermittedOptions().setAddress("outbound.address"));

        // Create the TCP EventBus bridge
        TcpEventBusBridge bridge = TcpEventBusBridge.create(vertx, bridgeOptions);

        // Listen on port 7000
        bridge.listen(7000).onComplete(res -> {
            if (res.succeeded()) {
                System.out.println("TCP EventBus bridge is listening on port 7000");
            } else {
                System.err.println("Failed to start the TCP EventBus bridge: " + res.cause());
            }
        });

        // Set up EventBus consumers
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer("inbound.address", message -> {
            JsonObject body = (JsonObject) message.body();
            System.out.println("Received a message on inbound.address: " + body);
            // Echo the message back
            eventBus.send("outbound.address", body);
        });
    }
}
