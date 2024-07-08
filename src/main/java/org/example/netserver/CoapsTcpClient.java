package org.example.netserver;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

public class CoapsTcpClient extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new CoapsTcpClient());
    }

    @Override
    public void start() {
        NetClientOptions options = new NetClientOptions();
        NetClient client = vertx.createNetClient(options);

        client.connect(5684, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected to server");
                NetSocket socket = res.result();

                // Create a CoAP request (this is a placeholder and not a valid CoAP request)
                Buffer request = Buffer.buffer("CoAP request");
                socket.write(request);

                socket.handler(buffer -> {
                    // Handle the response from the server
                    System.out.println("Received response: " + buffer.toString());
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
}

