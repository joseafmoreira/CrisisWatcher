package dev.joseafmoreira.db.server;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.List;

import dev.joseafmoreira.db.server.connection.Connection;
import dev.joseafmoreira.db.server.manager.Manager;

public class DBServer {
    private static final int PORT = 1433;
    private static final int BACKLOG = 50;
    private static final InetAddress ADDRESS = InetAddress.getLoopbackAddress();
    private ServerSocket serverSocket;
    private Manager manager;
    private List<Connection> connections;

    public DBServer() {

    }
}
