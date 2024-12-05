package dev.crisiswatcher.client.io.input;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import dev.crisiswatcher.client.dto.RoomDTO;
import dev.crisiswatcher.client.dto.UserDTO;

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
     * The user data transfer object
     */
    private UserDTO userDTO;
    /**
     * The room data transfer object
     */
    private RoomDTO roomDTO;

    /**
     * Constructs a new MulticastSocketInput object with a specified multicastSocket.
     * 
     * @param multicastSocket the specified multicastSocket
     */
    public MulticastSocketInput(MulticastSocket multicastSocket, UserDTO userDTO, RoomDTO roomDTO) {
        this.multicastSocket = multicastSocket;
        this.userDTO = userDTO;
        this.roomDTO = roomDTO;
    }

    /**
     * Receives messages from the multicastSocket and properly handles them accordingly.
     */
    @Override
    public void run() {
        while (true) {
            try {
                byte[] datagramPacketBuffer = new byte[TOTAL_BYTES];
                DatagramPacket datagramPacket = new DatagramPacket(datagramPacketBuffer, datagramPacketBuffer.length);
                multicastSocket.receive(datagramPacket);
                String message = new String(datagramPacket.getData());
                String[] splittedMessage = message.split(":");
                if (!splittedMessage[0].equals(userDTO.getName()) || splittedMessage[1].trim().startsWith("[Notificação]") || splittedMessage[1].trim().startsWith("[Pedido]")) {
                    System.out.println("[" + splittedMessage[0] + " -> " + roomDTO.getName() + "]: " + ((splittedMessage.length == 2) ? splittedMessage[1] : ""));
                }
            } catch (IOException e) {
                break;
            }
        }
        interrupt();
    }
}
