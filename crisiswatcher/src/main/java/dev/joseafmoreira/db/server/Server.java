package dev.joseafmoreira.db.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.joseafmoreira.db.server.socketconnection.SocketConnection;
import dev.joseafmoreira.log.LogHandler;

public class Server {
    private static final int PORT = 1433;
    private static final int BACKLOG = 50;
    private static final InetAddress ADDRESS = InetAddress.getLoopbackAddress();
    private ServerSocket serverSocket;
    private List<SocketConnection> connections;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT, BACKLOG, ADDRESS);
            LogHandler.addDBLogEntry("Servidor DB iniciado em " + ADDRESS.toString().split("/")[1] + ":" + PORT);
        } catch (IOException ignored) {
            LogHandler.addDBLogEntry("Erro ao iniciar o servidor DB");
            System.exit(0);
        }
    }

    public void start() {
        connections = Collections.synchronizedList(new ArrayList<>());
        while (true) {
            try {
                connections.add(new SocketConnection(serverSocket.accept()));
                checkConnections();
            } catch (IOException ignored) {}
        }
    }

    private void checkConnections() {
        for (SocketConnection socketConnection : connections) {
            if (socketConnection.isInterrupted()) {
                socketConnection.close();
                connections.remove(socketConnection);
            } else if (!socketConnection.isAlive()) socketConnection.start();
        }
    }
}
