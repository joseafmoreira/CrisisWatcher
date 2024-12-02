package dev.crisiswatcher.client.io.output;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import dev.crisiswatcher.client.io.IOSharedResources;

/**
 * Handles the socket output in a separate thread. <p>
 * 
 * The available constructors for this {@code SocketOutput} include: <p>
 * <ul>
 *  <li>{@link #SocketOutput(OutputStream, IOSharedResources)}: Constructs a new SocketOutput object with a specified socketOutputStream and ioSharedResources</li>
 * </ul> 
 * 
 * The operations available for this {@code StandardInput} include: <p>
 * <ul>
 *  <li>{@link #run()}: Send the messages that are inside the TCP output buffer through the socket output print writer</li>
 * </ul>
 * 
 * <h3>SocketOutput</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 * @see Thread
 */
public class SocketOutput extends Thread {
    /**
     * The socket output print writer
     */
    private PrintWriter socketOutput;
    /**
     * The TCP output buffer
     */
    private List<String> tcpOutputBuffer;

    /**
     * Constructs a new SocketOutput object with a specified socketOutputStream and ioSharedResources.
     * 
     * @param socketOutputStream the specified socket output stream
     * @param ioSharedResources the specified ioSharedResources
     */
    public SocketOutput(OutputStream socketOutputStream, IOSharedResources ioSharedResources) {
        socketOutput = new PrintWriter(socketOutputStream, true);
        tcpOutputBuffer = ioSharedResources.getTcpOutputBuffer();
    }

    /**
     * Send the messages that are inside the TCP output buffer through the socket output print writer.
     */
    @Override
    public void run() {
        while (true) {
            if (!tcpOutputBuffer.isEmpty()) socketOutput.println(tcpOutputBuffer.remove(0));
        }
    }
}
