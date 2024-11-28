package dev.crisiswatcher.client.io.input;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

/**
 * Handles the multicastSocket input in a separate thread. <p>
 * 
 * The available constructors for this {@code MulticastSocketInput} include: <p>
 * <ul>
 *  <li>{@link #MulticastSocketInput(MulticastSocket)}: Constructs a new MulticastSocketInput object with a specified multicastSocket</li>
 * </ul> 
 * 
 * The operations available for this {@code MulticastSocketInput} include: <p>
 * <ul>
 *  <li>{@link #run()}: Receives messages from the multicastSocket and properly handles them accordingly</li>
 * </ul>
 * 
 * <h3>MulticastSocketInput</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 * @see Thread
 */
public class MulticastSocketInput extends Thread {
    /**
     * The total number of bytes of the datagram packet buffer
     */
    private static final int TOTAL_BYTES = 1024;
    /**
     * The client's UDP socket
     */
    private MulticastSocket multicastSocket;

    /**
     * Constructs a new MulticastSocketInput object with a specified multicastSocket.
     * 
     * @param multicastSocket the specified multicastSocket
     */
    public MulticastSocketInput(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }

    /**
     * Receives messages from the multicastSocket and properly handles them accordingly.
     */
    @Override
    public void run() {
        byte[] datagramPacketBuffer = new byte[TOTAL_BYTES];
        DatagramPacket datagramPacket = new DatagramPacket(datagramPacketBuffer, datagramPacketBuffer.length);
        while (true) {
            try {
                multicastSocket.receive(datagramPacket);
                System.out.println(new String(datagramPacket.getData()));
            } catch (IOException e) {
                break;
            }
        }
        interrupt();
    }
}
