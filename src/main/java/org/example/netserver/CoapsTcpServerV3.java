package org.example.netserver;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.PemKeyCertOptions;

public class CoapsTcpServerV3 extends AbstractVerticle {

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
                        .setKeyPath("path/to/key.pem")
                        .setCertPath("path/to/cert.pem"));

        NetServer server = vertx.createNetServer(options);

        server.connectHandler(socket -> {
            socket.handler(buffer -> {
                // Parse CoAP message from buffer
                CoapMessage request = parseCoapMessage(buffer);
                if (request != null) {
                    handleCoapMessage(socket, request);
                } else {
                    System.err.println("Invalid CoAP message received.");
                }
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

    private CoapMessage parseCoapMessage(Buffer buffer) {
        // Parse the CoAP message from the buffer
        // This is a simplified parser for demonstration purposes
        // You need to implement the actual CoAP message parsing according to the CoAP protocol (RFC 7252)
        CoapMessage message = new CoapMessage();
        message.setPayload(buffer.toString());
        return message;
    }

    private void handleCoapMessage(NetSocket socket, CoapMessage request) {
        // Handle the CoAP request and generate a response
        System.out.println("Received CoAP request: " + request.getPayload());

        CoapMessage response = new CoapMessage();
        response.setPayload("Hello from CoAP server");

        // Send the response back to the client
        socket.write(Buffer.buffer(response.getPayload()));
    }

    private static class CoapMessage {
        private String payload;

        public String getPayload() {
            return payload;
        }

        public void setPayload(String payload) {
            this.payload = payload;
        }
    }
}

