package dev.crisiswatcher.client.io.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import dev.crisiswatcher.client.dto.RoomDTO;
import dev.crisiswatcher.client.dto.UserDTO;
import dev.crisiswatcher.client.dto.UserDTO.UserProfile;
import dev.crisiswatcher.client.handler.UnseenMessagesHandler;
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
    private static final String DEFAULT_ROOM_CHANGE_MESSAGE = "Entrou na sala de chat: ";
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
     * The TCP output buffer
     */
    private List<String> tcpOutputBuffer;
    /**
     * Unseen messages handler thread
     */
    private UnseenMessagesHandler unseenMessagesHandler;

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
        tcpOutputBuffer = ioSharedResources.getTcpOutputBuffer();
        unseenMessagesHandler = new UnseenMessagesHandler(tcpOutputBuffer);
    }

    /**
     * Receives messages from the socket input and properly handles them accordingly.
     */
    @Override
    public void run() {
        unseenMessagesHandler.start();
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
                    unseenMessagesHandler.setRunningState(userDTO.getName() != null);
                    if (!unseenMessagesHandler.isRunning()) {
                        roomDTO.setName(null);
                        roomDTO.setIp(null);
                        roomDTO.setPort(0);
                    } else {
                        tcpOutputBuffer.add("/connect Civil");
                    }
                } else if (output.startsWith("/room")) {
                    String[] splittedOutput = output.split(" ");
                    roomDTO.setName((splittedOutput[1].equals("null")) ? null : splittedOutput[1]);
                    try {
                        roomDTO.setIp((splittedOutput[2].equals("null")) ? null : InetAddress.getByName(splittedOutput[2]));
                    } catch (UnknownHostException ignored) {}
                    roomDTO.setPort(Integer.valueOf(splittedOutput[3]));
                    output = DEFAULT_ROOM_CHANGE_MESSAGE + roomDTO.getName();
                } else if (output.startsWith("/chat_msgs")) {
                    String[] splittedOutput = output.split(" ");
                    String outputMessage = "";
                    for (int i = 1; i < splittedOutput.length; i++) {
                        String[] splittedMessage = splittedOutput[i].split("/");
                        outputMessage += "[" + splittedMessage[0].trim() + " -> " + splittedMessage[1].trim() + "]: " + splittedMessage[2].trim().replaceAll("_", " ").trim() + "\n";
                    }
                    output = (outputMessage.equals("")) ? "" : outputMessage.substring(0, outputMessage.length() - 1);
                } else if (output.startsWith("/msgs")) {
                    String[] splittedOutput = output.split(" ");
                    String outputMessage = "";
                    for (int i = 1; i < splittedOutput.length; i++) {
                        String[] splittedMessage = splittedOutput[i].split("/");
                        outputMessage += "[" + splittedMessage[1].trim() + " -> " + splittedMessage[2].trim() + "]: " + splittedMessage[3].trim().replaceAll("_", " ").trim() + "\n";
                        if (Integer.valueOf(splittedMessage[4]).equals(0)) {
                            tcpOutputBuffer.add("/seen " + splittedMessage[0]);
                        }
                    }
                    output = (outputMessage.equals("")) ? "" : outputMessage.substring(0, outputMessage.length() - 1);
                } else if (output.equals("/seen")) {
                    continue;
                }
                if (!output.equals("")) System.out.println(output.equals(DEFAULT_OUTPUT_MESSAGE) ? INVALID_COMMAND_MESSAGE : output);
            }
        } catch (IOException ignored) {}
        interrupt();
    }
}
