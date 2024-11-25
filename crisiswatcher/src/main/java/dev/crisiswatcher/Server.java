package dev.crisiswatcher;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import dev.crisiswatcher.server.logger.Logger;
import dev.crisiswatcher.server.connection.ConnectionTCP;

public class Server {
    private static final int PORT = 27015;
    private static final int BACKLOG = 50;
    private static final InetAddress ADDRESS = InetAddress.getLoopbackAddress();
    private static final int TIMEOUT = 1000;
    private ServerSocket serverSocket;
    private List<ConnectionTCP> connections;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT, BACKLOG, ADDRESS);
            serverSocket.setSoTimeout(TIMEOUT);
            Logger.addServerLogEntry("O servidor foi iniciado com sucesso em " + ADDRESS.toString().split("/")[1] + ":" + PORT);
        } catch (IOException e) {
            Logger.addServerLogEntry("Erro ao iniciar o servidor: " + e.getMessage());
            System.exit(0);
        }
    }

    public void start() {
        connections = Collections.synchronizedList(new ArrayList<>());
        while (true) {
            try {
                connections.add(new ConnectionTCP(serverSocket.accept()));
            } catch (SocketTimeoutException ignored) {}
            catch (IOException e) {
                Logger.addServerLogEntry("Erro no servidor: " + e.getMessage());
                break;
            }
            checkConnections();
        }

        close();
    }

    private void close() {
        try {
            if (serverSocket != null) serverSocket.close();
            Iterator<ConnectionTCP> it = connections.iterator();
            if (it.hasNext()) {
                ConnectionTCP connection = it.next();
                connection.close();
                it.remove();
            }
        } catch (IOException ignored) {}
    }

    private void checkConnections() {
        Iterator<ConnectionTCP> it = connections.iterator();
        while (it.hasNext()) {
            ConnectionTCP connection = it.next();
            if (connection.isInterrupted()) {
                connection.close();
                it.remove();
                continue;
            } else if (!connection.isAlive()) {
                try {
                    connection.start();
                } catch (IllegalThreadStateException ignored) {
                    it.remove();
                }
            }
        }
    }

    public static void main(String[] args) {
        (new Server()).start();
    }
}
