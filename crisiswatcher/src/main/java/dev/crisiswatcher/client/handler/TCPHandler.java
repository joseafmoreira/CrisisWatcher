package dev.crisiswatcher.client.handler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

import dev.crisiswatcher.client.io.IOSharedResources;
import dev.crisiswatcher.client.io.input.SocketInput;
import dev.crisiswatcher.client.io.output.SocketOutput;

/**
 * Handles the TCP connection of the client in a separate thread. <p>
 * 
 * The available constructors for this {@code TCPHandler} include: <p>
 * <ul>
 *  <li>{@link #TCPHandler(IOSharedResources)}: Constructs a new TCPHandler thread with a specified ioSharedResources</li>
 * </ul>
 * 
 * The operations available for this {@code TCPHandler} include:
 * <ul>
 *  <li>{@link #run()}: Handles the state of the socket input and output threads</li>
 * </ul>
 * 
 * <h3>TCPHandler</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 * @see Thread
 */
public class TCPHandler extends Thread {
    /**
     * The server's ip address
     */
    private static final InetAddress ADDRESS = InetAddress.getLoopbackAddress();
    /**
     * The server's port
     */
    private static final int PORT = 27015;
    /**
     * The client's TCP socket
     */
    private Socket tcpSocket;
    /**
     * The client's TCP socket input thread
     */
    private SocketInput socketInput;
    /**
     * The client's TCP socket output thread
     */
    private SocketOutput socketOutput;

    /**
     * Constructs a new TCPHandler thread with a specified ioSharedResources.
     * 
     * @param ioSharedResources the specified I/O shared resources
     * @throws IOException if there's an exception launched by the TCP socket creation
     */
    public TCPHandler(IOSharedResources ioSharedResources) throws IOException {
        tcpSocket = new Socket(ADDRESS, PORT);
        socketInput = new SocketInput(new InputStreamReader(tcpSocket.getInputStream()), ioSharedResources);
        socketOutput = new SocketOutput(tcpSocket.getOutputStream(), ioSharedResources);
    }

    /**
     * Handles the state of the socket input and output threads.
     */
    @Override
    public void run() {
        socketInput.start();
        socketOutput.start();
        while (!socketInput.isInterrupted() && !socketOutput.isInterrupted()) {}
        interrupt();
    }
}
