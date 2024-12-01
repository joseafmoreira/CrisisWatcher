package dev.crisiswatcher.client.handler;

import java.io.IOException;
import java.util.List;

import dev.crisiswatcher.client.dto.RoomDTO;
import dev.crisiswatcher.client.dto.UserDTO;
import dev.crisiswatcher.client.handler.udp.UDPConnection;
import dev.crisiswatcher.client.io.IOSharedResources;


/**
 * Handles the UDP connection of the client in a separate thread. <p>
 * 
 * The available constructors for this {@code UDPHandler} include: <p>
 * <ul>
 *  <li>{@link #UDPHandler(IOSharedResources)}: Constructs a new UDPHandler thread with a specified ioSharedResources</li>
 * </ul>
 * 
 * The operations available for this {@code UDPHandler} include:
 * <ul>
 *  <li>{@link #run()}: Checks if the room has changed and handles the UDP connection thread</li>
 * </ul>
 * 
 * <h3>UDPHandler</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 * @see Thread
 */
public class UDPHandler extends Thread {
    /**
     * The UDP Connection instance
     */
    private UDPConnection udpConnection;
    /**
     * The user data transfer object
     */
    private UserDTO userDTO;
    /**
     * The previous room data transfer object
     */
    private RoomDTO previousRoom;
    /**
     * The current room data transfer object
     */
    private RoomDTO currentRoom;
    /**
     * The TCP output buffer
     */
    private List<String> tcpOutputBuffer;
    /**
     * The UDP output buffer
     */
    private List<String> udpOutputBuffer;

    /**
     * Constructs a new UDP Handler instace with a specified ioSharedResources.
     * 
     * @param ioSharedResources the specified ioSharedResources
     */
    public UDPHandler(IOSharedResources ioSharedResources) throws IOException {
        udpConnection = null;
        userDTO = ioSharedResources.getUserDTO();
        previousRoom = new RoomDTO();
        currentRoom = ioSharedResources.getRoomDTO();
        tcpOutputBuffer = ioSharedResources.getTcpOutputBuffer();
        udpOutputBuffer = ioSharedResources.getUdpOutputBuffer();
    }

    /**
     * Checks if the room has changed and handles the UDP connection thread.
     */
    @Override
    public void run() {
        while (true) {
            if (!previousRoom.equals(currentRoom)) {
                previousRoom = new RoomDTO(currentRoom);
                if (previousRoom.isValid()) {
                    try {
                        udpConnection = new UDPConnection(previousRoom, userDTO, udpOutputBuffer);
                        udpConnection.start();
                        tcpOutputBuffer.add("/group " + previousRoom.getName());
                    } catch (IOException ignored) {
                        ignored.printStackTrace();
                        break;
                    }
                } else {
                    udpConnection = null;
                    System.out.println("Desconectou-se das salas de chat");
                }
                udpOutputBuffer.clear();
            }
            try {
                sleep(500);
            } catch (InterruptedException ignored) {} 
        }
        interrupt();
    }
}
