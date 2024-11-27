package dev.crisiswatcher.client.handler.udp;

import java.io.IOException;
import java.net.MulticastSocket;
import java.util.List;

import dev.crisiswatcher.client.dto.RoomDTO;
import dev.crisiswatcher.client.dto.UserDTO;
import dev.crisiswatcher.client.io.input.MulticastSocketInput;
import dev.crisiswatcher.client.io.output.MulticastSocketOutput;

/**
 * Handles the UDP connection in a separate thread. <p>
 * 
 * The available constructors for this {@code MulticastSocketOutput} include: <p>
 * <ul>
 *  <li>{@link #UDPConnection(RoomDTO, UserDTO, List)}: Constructs a new UDPCOnnection with a specified roomDTO, userDTO and udpOutputBuffer</li>
 * </ul> 
 * 
 * The operations available for this {@code MulticastSocketOutput} include: <p>
 * <ul>
 *  <li>{@link #run()}: Handles the state of the multicastSocket input and output threads</li>
 * </ul>
 * 
 * <h3>MulticastSocketOutput</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 * @see Thread
 */
public class UDPConnection extends Thread {
    /**
     * The client's UDP socket
     */
    private MulticastSocket multicastSocket;
    /**
     * The client's UDP socket input thread
     */
    private MulticastSocketInput multicastSocketInput;
    /**
     * The client's UDP socket output thread
     */
    private MulticastSocketOutput multicastSocketOutput;

    /**
     * Constructs a new UDPCOnnection with a specified roomDTO, userDTO and udpOutputBuffer.
     * 
     * @param roomDTO the specified roomDTO
     * @param userDTO the specified userDTO
     * @param udpOutputBuffer the specified udpOutputBuffer
     * @throws IOException if there's an exception launched by the UDP socket creation
     */
    @SuppressWarnings("deprecation")
    public UDPConnection(RoomDTO roomDTO, UserDTO userDTO, List<String> udpOutputBuffer) throws IOException {
        multicastSocket = new MulticastSocket(roomDTO.getPort());
        multicastSocket.joinGroup(roomDTO.getIp());
        multicastSocketInput = new MulticastSocketInput(multicastSocket);
        multicastSocketOutput = new MulticastSocketOutput(multicastSocket, userDTO, udpOutputBuffer);
    }

    /**
     * Handles the state of the multicastSocket input and output threads.
     */
    @Override
    public void run() {
        multicastSocketInput.start();
        multicastSocketOutput.start();
        while (!multicastSocketInput.isInterrupted() && !multicastSocketOutput.isInterrupted()) {}
        interrupt();
    }
}
