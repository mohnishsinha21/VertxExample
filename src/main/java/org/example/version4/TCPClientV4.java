package org.example.version4;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPClientV4 {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        // Using a fixed thread pool with 4 threads (one for each client)
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 1; i <= 4; i++) {
            final int clientId = i;
            executor.execute(() -> connectAndSendRequests(vertx, clientId));
        }

        // Shutdown the executor properly when all tasks are completed
        executor.shutdown();
    }

    private static void connectAndSendRequests(Vertx vertx, int clientId) {
        NetClient client = vertx.createNetClient();

        client.connect(8889, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Client connected to the server for Client ID: " + clientId);
                NetSocket socket = res.result();

                // Send requests sequentially
                sendSignin(socket, clientId, () ->
                        sendPing(socket, clientId, () ->
                                sendPing(socket, clientId, () ->
                                        sendPing(socket, clientId, () ->
                                                sendSignout(socket, clientId, socket::close)
                                        )
                                )
                        )
                );
            } else {
                System.out.println("Client failed to connect to the server for Client ID: " + clientId + ": " + res.cause());
            }
        });
    }

    private static void sendSignin(NetSocket socket, int clientId, Runnable onComplete) {
        sendRequest(socket, "/signin " + clientId, onComplete);
    }

    private static void sendPing(NetSocket socket, int clientId, Runnable onComplete) {
        sendRequest(socket, "/ping " + clientId, onComplete);
    }

    private static void sendSignout(NetSocket socket, int clientId, Runnable onComplete) {
        sendRequest(socket, "/signout " + clientId, onComplete);
    }

    private static void sendRequest(NetSocket socket, String request, Runnable onComplete) {
        System.out.println("Sending request: " + request);
        socket.write(request + "\n");

        // Handle response from the server
        socket.handler(buffer -> {
            System.out.println("Response: " + buffer.toString());

            // After receiving response, trigger the next request
            socket.handler(null); // Clear the handler to avoid duplicate calls for the same response
            onComplete.run();
        });
    }
}