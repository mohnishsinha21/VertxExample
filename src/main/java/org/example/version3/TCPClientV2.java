package org.example.version3;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClientV2 {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 4; i++) {
            final int clientId = i;
            executor.execute(() -> {
                vertx.createNetClient().connect(8888, "localhost", res -> {
                    if (res.succeeded()) {
                        System.out.println("Client connected to the server for Client ID: " + clientId);
                        NetSocket socket = res.result();

                        sendRequest(socket, "/signin " + clientId);
                        sendRequest(socket, "/ping " + clientId);
                        sendRequest(socket, "/ping " + clientId);
                        sendRequest(socket, "/ping " + clientId);
                        sendRequest(socket, "/signout");
                        socket.close();
                    } else {
                        System.out.println("Client failed to connect to the server for Client ID: " + clientId + ": " + res.cause());
                    }
                });
            });
        }

        executor.shutdown();
    }

    private static void sendRequest(NetSocket socket, String request) {
        System.out.println("Sending request: " + request);
        socket.write(request + "\n");

        // Handle responses from the server
        socket.handler(buffer -> {
            System.out.println("Response: " + buffer.toString());
        });
    }
}