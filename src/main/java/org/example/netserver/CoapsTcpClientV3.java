package org.example.netserver;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.PemTrustOptions;

public class CoapsTcpClientV3 extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new CoapsTcpClient());
    }

    @Override
    public void start() {
        NetClientOptions options = new NetClientOptions()
                .setSsl(true)
                .setTrustOptions(new PemTrustOptions()
                        .addCertPath("path/to/cert.pem"));

        NetClient client = vertx.createNetClient(options);

        client.connect(5684, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected to server");
                NetSocket socket = res.result();

                // Create a CoAP request
                CoapMessage request = new CoapMessage();
                request.setPayload("CoAP request");

                socket.write(Buffer.buffer(request.getPayload()));

                socket.handler(buffer -> {
                    // Parse the CoAP response
                    CoapMessage response = new CoapMessage();
                    response.setPayload(buffer.toString());
                    System.out.println("Received response: " + response.getPayload());
                });

                socket.closeHandler(v -> {
                    System.out.println("Connection closed");
                });

                socket.exceptionHandler(t -> {
                    System.err.println("Error: " + t.getMessage());
                });
            } else {
                System.out.println("Failed to connect to server: " + res.cause());
            }
        });
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

