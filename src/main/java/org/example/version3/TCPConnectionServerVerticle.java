package org.example.version3;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;

public class TCPConnectionServerVerticle extends AbstractVerticle {

    @Override
    public void start() {
        NetServer server = vertx.createNetServer();

        server.connectHandler(socket -> {
            System.out.println("New client connected: " + socket.remoteAddress());

            socket.handler(buffer -> {
                String request = buffer.toString();
                System.out.println("Received message from client: " + request);

                // Extract request ID
                int requestId = Integer.parseInt(request.replaceAll("[^\\d]", ""));

                // Process request and respond
                String response = "Response for Request " + requestId + ": Message received and handled by : " + socket.remoteAddress();
                socket.write(response + "\n");
            });

            socket.closeHandler(close -> {
                System.out.println("Client disconnected: " + socket.remoteAddress());
            });
        });

        server.listen(8888, "0.0.0.0", res -> {
            if (res.succeeded()) {
                System.out.println("Server is listening on port 8888");
            } else {
                System.out.println("Server failed to bind: " + res.cause());
            }
        });
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TCPConnectionServerVerticle());
    }
}

