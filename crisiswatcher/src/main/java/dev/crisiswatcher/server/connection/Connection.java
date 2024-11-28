package dev.crisiswatcher.server.connection;

import java.io.IOException;
import java.net.Socket;

import dev.crisiswatcher.server.connection.tcp.TCPConnection;

/**
 * Handles the client connection to the server in a separate thread.
 * 
 * The available constructors for this {@code Connection} include: <p>
 * <ul>
 *  <li>{@link #Connection(Socket)}: Constructs a new Connection thread with a specified clientSocket</li>
 * </ul>
 * 
 * The available operations for this {@code Connection} include: <p>
 * <ul>
 *  <li>{@link #run()}: Handles the server's TCP and UDP connections threads</li>
 * </ul>
 * 
 * <h3>Connection</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 */
public class Connection extends Thread {
    /**
     * The server's TCP connection thread
     */
    private TCPConnection tcpConnection;

    /**
     * Constructs a new Connection thread with a specified clientSocket.
     * 
     * @param clientSocket the specified clientSocket
     */
    public Connection(Socket clientSocket) {
        try {
            tcpConnection = new TCPConnection(clientSocket);
        } catch (IOException ignored) {
            interrupt();
        }
    }

    /**
     * Handles the server's TCP and UDP connections threads.
     */
    @Override
    public void run() {
        tcpConnection.start();
        while (!tcpConnection.isInterrupted()) {}
        interrupt();
    }
}
