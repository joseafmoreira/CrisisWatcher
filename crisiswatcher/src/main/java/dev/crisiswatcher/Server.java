package dev.crisiswatcher;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import dev.crisiswatcher.server.connection.tcp.TCPConnection;
import dev.crisiswatcher.server.logger.Logger;

/**
 * Represents the server's main entry class
 * 
 * The available constructors for this {@code Server} include: <p>
 * <ul>
 *  <li>{@link #Server()}: Constructs a new Server object</li>
 * </ul>
 * 
 * The operations available for this {@code Server} include:
 * <ul>
 *  <li>{@link #start()}: Initializes the server process</li>
 *  <li>{@link #checkConnections()}: Checks the connections added to this server's list</li>
 *  <li>{@link #main(String[])}: Represents the server main entry function</li>
 * </ul>
 * 
 * <h3>Server</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 */
public class Server {
    /**
     * The server's socket port
     */
    private static final int PORT = 27015;
    /**
     * The server's socket backlog
     */
    private static final int BACKLOG = 50;
    /**
     * The server's socket address
     */
    private static final InetAddress ADDRESS = InetAddress.getLoopbackAddress();
    /**
     * The server's socket timeout
     */
    private static final int TIMEOUT = 1000;
    /**
     * The server's socket
     */
    private ServerSocket serverSocket;
    /**
     * The server's connection list
     */
    private List<TCPConnection> connections;

    /**
     * Constructs a new Server object.
     */
    public Server() {
        try {
            serverSocket = new ServerSocket(PORT, BACKLOG, ADDRESS);
            serverSocket.setSoTimeout(TIMEOUT);
            connections = Collections.synchronizedList(new ArrayList<>());
            Logger.addServerLogEntry("O servidor foi iniciado com sucesso em " + ADDRESS.toString().split("/")[1] + ":" + PORT);
        } catch (IOException e) {
            Logger.addServerLogEntry("Erro ao iniciar o servidor: " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Initializes the server process.
     */
    public void start() {
        while (true) {
            try {
                connections.add(new TCPConnection(serverSocket.accept()));
            } catch (SocketTimeoutException ignored) {}
            catch (IOException e) {
                Logger.addServerLogEntry("Erro no servidor: " + e.getMessage());
                break;
            }
            checkConnections();
        }
        System.exit(0);
    }

    /**
     * Checks the connections added to this server's list.
     */
    private void checkConnections() {
        Iterator<TCPConnection> it = connections.iterator();
        while (it.hasNext()) {
            TCPConnection connection = it.next();
            if (connection.isInterrupted()) {
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

    /**
     * Represents the server main entry function.
     * 
     * @param args the specified args at the start of the program
     */
    public static void main(String[] args) {
        (new Server()).start();
    }
}
