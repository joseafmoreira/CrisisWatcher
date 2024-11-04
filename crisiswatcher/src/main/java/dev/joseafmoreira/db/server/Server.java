package dev.joseafmoreira.db.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Server {
    private static final int port = 1433;
    private static final int backlog = 50;
    private static final InetAddress address = InetAddress.getLoopbackAddress();
    private ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(port, backlog, address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
