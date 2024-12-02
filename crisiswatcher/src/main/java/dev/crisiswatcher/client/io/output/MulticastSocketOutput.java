package dev.crisiswatcher.client.io.output;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.List;

import dev.crisiswatcher.client.dto.RoomDTO;
import dev.crisiswatcher.client.dto.UserDTO;

/**
 * Handles the multicastSocket input in a separate thread. <p>
 * 
 * The available constructors for this {@code MulticastSocketOutput} include: <p>
 * <ul>
 *  <li>{@link #MulticastSocketOutput(MulticastSocket, UserDTO, List)}: Constructs a new MulticastSocketOutput object with a specified multicastSocket, userDTO and udpOutputBuffer</li>
 * </ul> 
 * 
 * The operations available for this {@code MulticastSocketOutput} include: <p>
 * <ul>
 *  <li>{@link #run()}: Sends messages from the UDP output buffer to the multicastSocket</li>
 * </ul>
 * 
 * <h3>MulticastSocketOutput</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 * @see Thread
 */
public class MulticastSocketOutput extends Thread {
    /**
     * The client's UDP socket
     */
    private MulticastSocket multicastSocket;
    /**
     * The user data transfer object
     */
    private UserDTO userDTO;
    /**
     * The user data transfer object
     */
    private RoomDTO roomDTO;
    /**
     * The UDP output buffer
     */
    private List<String> udpOutputBuffer;

    /**
     * Constructs a new MulticastSocketOutput object with a specified multicastSocket, userDTO, roomDTO and udpOutputBuffer.
     * 
     * @param multicastSocket the specified multicastSocket
     * @param userDTO the specified userDTO
     * @param roomDTO the specified roomDTO
     * @param udpOutputBuffer the specified udpOutputBuffer
     */
    public MulticastSocketOutput(MulticastSocket multicastSocket, UserDTO userDTO, RoomDTO roomDTO, List<String> udpOutputBuffer) {
        this.multicastSocket = multicastSocket;
        this.userDTO = userDTO;
        this.roomDTO = roomDTO;
        this.udpOutputBuffer = udpOutputBuffer;
    }

    /**
     * Sends messages from the UDP output buffer to the multicastSocket.
     */
    @Override
    public void run() {
        while (true) {
            if (!udpOutputBuffer.isEmpty()) {
                try {
                    String message = userDTO.getName() + ": " + udpOutputBuffer.remove(0);
                    byte[] datagramPacketBuffer = message.getBytes(StandardCharsets.UTF_8);
                    multicastSocket.send(new DatagramPacket(datagramPacketBuffer, datagramPacketBuffer.length, roomDTO.getIp(), roomDTO.getPort()));
                } catch (IndexOutOfBoundsException ignored) {}
                catch (IOException e) {
                    break;
                }
            }
        }
        interrupt();
    }
}
