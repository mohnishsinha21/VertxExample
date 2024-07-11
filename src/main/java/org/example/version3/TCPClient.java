package org.example.version3;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClient {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 10; i++) {
            final int requestNum = i;
            executor.execute(() -> {
                vertx.createNetClient().connect(8888, "localhost", res -> {
                    if (res.succeeded()) {
                        System.out.println("Client connected to the server for Request " + requestNum);
                        NetSocket socket = res.result();

                        // Send a message with unique ID to the server
                        String message = "Request" + requestNum + ": Hello from Client";
                        socket.write(message + "\n");

                        // Handle messages received from the server
                        socket.handler(buffer -> {
                            System.out.println("Response for Request " + requestNum + ": " + buffer.toString());
                        });

                    } else {
                        System.out.println("Client failed to connect to the server for Request " + requestNum + ": " + res.cause());
                    }
                });
            });
        }

        executor.shutdown();
    }
}
