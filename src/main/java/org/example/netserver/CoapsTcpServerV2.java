package org.example.netserver;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.PemKeyCertOptions;

public class CoapsTcpServerV2 extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new CoapsTcpServer());
    }

    @Override
    public void start() {
        NetServerOptions options = new NetServerOptions()
                .setPort(5684) // Default port for CoAPS
                .setSsl(true)
                .setPemKeyCertOptions(new PemKeyCertOptions()
                        .setKeyPath("\"C:\\Users\\mohni\\key.pem\"")
                        .setCertPath("\"C:\\Users\\mohni\\cert.pem\""));

        NetServer server = vertx.createNetServer(options);

        server.connectHandler(socket -> {
            socket.handler(buffer -> {
                // Parse CoAP message from buffer
                handleCoapMessage(socket, buffer);
            });

            socket.closeHandler(v -> {
                System.out.println("Connection closed");
            });

            socket.exceptionHandler(t -> {
                System.err.println("Error: " + t.getMessage());
            });
        });

        server.listen(res -> {
            if (res.succeeded()) {
                System.out.println("CoAPS TCP server is now listening on port " + server.actualPort());
            } else {
                System.out.println("Failed to bind server: " + res.cause());
            }
        });
    }

    private void handleCoapMessage(NetSocket socket, Buffer buffer) {
        // Here you would parse the CoAP message and handle it
        // For demonstration, let's assume we simply echo the message back
        System.out.println("Received CoAP message: " + buffer.toString());

        // Create a CoAP response (this is a placeholder and not a valid CoAP response)
        Buffer response = Buffer.buffer("CoAP response");
        socket.write(response);
    }
}

