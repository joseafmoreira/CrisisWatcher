package dev.crisiswatcher.server.connection.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import dev.crisiswatcher.server.schema.User;

/**
 * Handles the TCP client connection to the server in a separate thread.
 * 
 * The available constructors for this {@code TCPConnection} include: <p>
 * <ul>
 *  <li>{@link #TCPConnection(Socket)}: Constructs a new TCPConnection thread with a specified clientSocket</li>
 * </ul>
 * 
 * The available operations for this {@code TCPConnection} include: <p>
 * <ul>
 *  <li>{@link #run()}: </li>
 * </ul>
 * 
 * <h3>TCPConnection</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 * @see Thread
 */
public class TCPConnection extends Thread {
    /**
     * Default output message of the TCP socket
     */
    private static final String DEFAULT_OUTPUT_MESSAGE = "O comando é inválido";
    /**
     * The server's TCP socket
     */
    private Socket clientSocket;
    /**
     * The server's TCP socket input buffered reader
     */
    private BufferedReader socketInput;
    /**
     * The server's TCP socket output print writer
     */
    private PrintWriter socketOutput;

    /**
     * The user associated with connection
     */
    private User user;

    /**
     * Constructs a new TCPConnection thread with a specified clientSocket.
     * 
     * @param clientSocket the specified clientSocket
     */
    public TCPConnection(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    /**
     * 
     */
    @Override
    public void run() {

    }

    /**
     * Returns user
     * @return
     */
    public User getUser(){
        return user;
    }
}
