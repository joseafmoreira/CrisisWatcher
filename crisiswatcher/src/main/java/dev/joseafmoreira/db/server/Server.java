package dev.joseafmoreira.db.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class Server {
    private static final int PORT = 1433;
    private static final int BACKLOG = 50;
    private static final InetAddress ADDRESS = InetAddress.getLoopbackAddress();
    private static final int SO_TIMEOUT = 2500;
    private ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT, BACKLOG, ADDRESS);
            serverSocket.setSoTimeout(SO_TIMEOUT);
        } catch (IOException e) {
            
        }
    }
}
