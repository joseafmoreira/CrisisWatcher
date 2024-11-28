package dev.crisiswatcher.client.io.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import dev.crisiswatcher.client.dto.RoomDTO;
import dev.crisiswatcher.client.dto.UserDTO;
import dev.crisiswatcher.client.dto.UserDTO.UserProfile;
import dev.crisiswatcher.client.io.IOSharedResources;

/**
 * Handles the socket input in a separate thread. <p>
 * 
 * The available constructors for this {@code SocketInput} include: <p>
 * <ul>
 *  <li>{@link #SocketInput(InputStreamReader, IOSharedResources)}: Constructs a new SocketInput object with a specified socketInputStreamReader and ioSharedResources</li>
 * </ul> 
 * 
 * The operations available for this {@code SocketInput} include: <p>
 * <ul>
 *  <li>{@link #run()}: Receives messages from the socket input and properly handles them accordingly</li>
 * </ul>
 * 
 * <h3>SocketInput</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 * @see Thread
 */
public class SocketInput extends Thread {
    /**
     * Message received from the socket input buffered reader when an invalid command is entered
     */
    private static final String DEFAULT_OUTPUT_MESSAGE = "O comando é inválido";
    /**
     * Message displayed when an invalid command is entered
     */
    private static final String INVALID_COMMAND_MESSAGE = DEFAULT_OUTPUT_MESSAGE + "\nDigite /help para obter a lista de comandos disponíveis";
    /**
     * Message displayed when you close the connection
     */
    private static final String DEFAULT_CLOSE_MESSAGE = "Até à próxima";
    /**
     * Message displayed when the room is changed
     */
    private static final String DEFAULT_ROOM_CHANGE_MESSAGE = "Sala alterada";
    /**
     * The socket input buffered reader
     */
    private BufferedReader socketInput;
    /**
     * The user data transfer object
     */
    private UserDTO userDTO;
    /**
     * The room data transfer object
     */
    private RoomDTO roomDTO;

    /**
     * Constructs a new SocketInput object with a specified socketInputStreamReader and ioSharedResources.
     * 
     * @param socketInputStreamReader the specified socketInputStreamReader
     * @param ioSharedResources the specified ioSharedResources
     */
    public SocketInput(InputStreamReader socketInputStreamReader, IOSharedResources ioSharedResources) {
        socketInput = new BufferedReader(socketInputStreamReader);
        userDTO = ioSharedResources.getUserDTO();
        roomDTO = ioSharedResources.getRoomDTO();
    }

    /**
     * Receives messages from the socket input and properly handles them accordingly.
     */
    @Override
    public void run() {
        String output;
        try {
            while ((output = socketInput.readLine()) != null) {
                if (output.equals("/close")) {
                    System.out.println(DEFAULT_CLOSE_MESSAGE);
                    break;
                } else if (output.startsWith("/user")) {
                    String[] splittedOutput = output.split(" ");
                    userDTO.setName((splittedOutput[1].equals("null")) ? null : splittedOutput[1]);
                    userDTO.setProfile((splittedOutput[2].equals("null")) ? null : UserProfile.getEnum(splittedOutput[2]));
                    output = splittedOutput[3].replaceAll("_", " ");
                } else if (output.startsWith("/room")) {
                    String[] splittedOutput = output.split(" ");
                    roomDTO.setName((splittedOutput[1].equals("null")) ? null : splittedOutput[1]);
                    try {
                        roomDTO.setIp((splittedOutput[2].equals("null")) ? null : InetAddress.getByName(splittedOutput[2]));
                    } catch (UnknownHostException ignored) {}
                    roomDTO.setPort(Integer.valueOf(splittedOutput[3]));
                    output = DEFAULT_ROOM_CHANGE_MESSAGE;
                }
                System.out.println(output.equals(DEFAULT_OUTPUT_MESSAGE) ? INVALID_COMMAND_MESSAGE : output);
            }
        } catch (IOException ignored) {}
        interrupt();
    }
}
