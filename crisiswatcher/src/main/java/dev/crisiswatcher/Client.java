package dev.crisiswatcher;

import java.io.IOException;

import dev.crisiswatcher.client.handler.TCPHandler;
import dev.crisiswatcher.client.handler.UDPHandler;
import dev.crisiswatcher.client.io.IOSharedResources;
import dev.crisiswatcher.client.io.input.StandardInput;

/**
 * Represents the client's main entry class
 * 
 * The available constructors for this {@code Client} include: <p>
 * <ul>
 *  <li>{@link #Client()}: Constructs a new Client object</li>
 * </ul>
 * 
 * The operations available for this {@code IOSharedResources} include:
 * <ul>
 *  <li>{@link #start()}: Initializes the client process</li>
 *  <li>{@link #main(String[])}: Represents the client main entry function</li>
 * </ul>
 */
public class Client {
    /**
     * The I/O threads shared resources
     */
    private IOSharedResources ioSharedResources;
    /**
     * The standard input thread
     */
    private StandardInput standardInput;
    /**
     * The TCP Handler
     */
    private TCPHandler tcpHandler;
    /**
     * The UDP Handler
     */
    private UDPHandler udpHandler;

    /**
     * Constructs a new Client object.
     */
    private Client() {
        try {
            ioSharedResources = new IOSharedResources();
            standardInput = new StandardInput(ioSharedResources);
            tcpHandler = new TCPHandler(ioSharedResources);
            udpHandler = new UDPHandler(ioSharedResources);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Initializes the client process.
     */
    public void start() {
        standardInput.start();
        tcpHandler.start();
        udpHandler.start();
        while (!standardInput.isInterrupted() && !tcpHandler.isInterrupted() && !udpHandler.isInterrupted()) {}
        System.exit(0);
    }

    /**
     * Represents the client main entry function.
     * 
     * @param args the specified args at the start of the program
     */
    public static void main(String[] args) {
        (new Client()).start();
    }
}
