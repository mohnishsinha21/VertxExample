package org.example.version3;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

import java.util.HashMap;
import java.util.Map;

public class TCPConnectionServerVerticleV2 extends AbstractVerticle {

    private Map<String, NetSocket> clientConnections = new HashMap<>();

    @Override
    public void start() {
        NetServer server = vertx.createNetServer();

        server.connectHandler(socket -> {
            System.out.println("New client connected: " + socket.remoteAddress());

            // Handling client disconnections
            socket.closeHandler(close -> {
                for (Map.Entry<String, NetSocket> entry : clientConnections.entrySet()) {
                    if (entry.getValue() == socket) {
                        clientConnections.remove(entry.getKey());
                        System.out.println("Client " + entry.getKey() + " disconnected");
                        break;
                    }
                }
            });

            socket.handler(buffer -> {
                String request = buffer.toString();
                System.out.println("Received message from client: " + request);

                String[] requestParts = request.split("\\s+");
                String apiEndpoint = requestParts[0];

                switch (apiEndpoint) {
                    case "/signin":
                        handleSignIn(socket, requestParts);
                        break;
                    case "/ping":
                        handlePing(socket, requestParts);
                        break;
                    case "/signout":
                        handleSignOut(socket);
                        break;
                    default:
                        socket.write("Invalid API endpoint\n");
                        break;
                }
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

    private void handleSignIn(NetSocket socket, String[] requestParts) {
        String clientId = requestParts[1];
        clientConnections.put(clientId, socket);
        socket.write("Welcome, Client " + clientId + " - You are signed in\n");
    }

    private void handlePing(NetSocket socket, String[] requestParts) {
        String clientId = requestParts[1];
        NetSocket clientSocket = clientConnections.get(clientId);
        if (clientSocket != null) {
            clientSocket.write("Ping request received from Client " + clientId + "\n");
        } else {
            socket.write("Client " + clientId + " is not signed in\n");
        }
    }

    private void handleSignOut(NetSocket socket) {
        for (Map.Entry<String, NetSocket> entry : clientConnections.entrySet()) {
            if (entry.getValue() == socket) {
                clientConnections.remove(entry.getKey());
                socket.write("You are signed out\n");
                break;
            }
        }
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new TCPConnectionServerVerticle());
    }
}