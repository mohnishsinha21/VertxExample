package org.example.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeOptions;
import io.vertx.ext.eventbus.bridge.tcp.TcpEventBusBridge;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

public class TcpClient {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        NetClient client = vertx.createNetClient();
        client.connect(7000, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected to server");
                NetSocket socket = res.result();

                // Send a message to the server
                JsonObject message = new JsonObject().put("type", "send")
                        .put("address", "inbound.address")
                        .put("body", new JsonObject().put("content", "Hello, server!"));
                socket.write(Buffer.buffer(message.encode()));

                // Handle incoming messages
                socket.handler(buffer -> {
                    JsonObject response = buffer.toJsonObject();
                    System.out.println("Received response: " + response);
                });
            } else {
                System.err.println("Failed to connect to server: " + res.cause());
            }
        });
    }
}
