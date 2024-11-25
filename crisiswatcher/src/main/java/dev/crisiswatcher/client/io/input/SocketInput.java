package dev.crisiswatcher.client.io.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import dev.crisiswatcher.client.dto.RoomDTO;
import dev.crisiswatcher.client.dto.UserDTO;
import dev.crisiswatcher.client.dto.UserDTO.UserProfile;
import dev.crisiswatcher.client.io.IOSharedResources;
import dev.crisiswatcher.client.udp.UDPHandler;

public class SocketInput extends Thread {
    private static final String DEFAULT_OUTPUT_MESSAGE = "O comando é inválido";
    private static final String INVALID_COMMAND_MESSAGE = DEFAULT_OUTPUT_MESSAGE + "\nDigite /help para obter a lista de comandos disponíveis";
    private IOSharedResources ioSharedResources;
    private BufferedReader socketInput;

    public SocketInput(IOSharedResources ioSharedResources, InputStream socketInputStream) {
        this.ioSharedResources = ioSharedResources;
        socketInput = new BufferedReader(new InputStreamReader(socketInputStream));
    }

    @Override
    public void run() {
        UserDTO userDTO = ioSharedResources.getUserDTO();
        RoomDTO roomDTO = ioSharedResources.getRoomDTO();
        String output;
        try {
            while ((output = socketInput.readLine()) != null) {
                if (output.equals("/close")) {
                    System.out.println("Até à próxima");
                    break;
                } else if (output.startsWith("/user")) {
                    String[] splittedOutput = output.split(" ");
                    userDTO.setUuid(Integer.valueOf(splittedOutput[1]));
                    userDTO.setUsername((splittedOutput[2].equals("null")) ? null : splittedOutput[2]);
                    userDTO.setProfile((splittedOutput[3].equals("null")) ? null : UserProfile.getEnum(splittedOutput[3]));
                    output = splittedOutput[4].replaceAll("_", " ");
                } else if (output.startsWith("/room")) {
                    String[] splittedOutput = output.split(" ");
                    try {
                        roomDTO.setName((splittedOutput[1].equals("null")) ? null : splittedOutput[1]);
                        roomDTO.setAddress((splittedOutput[1].equals("null")) ? null : InetAddress.getByName(splittedOutput[2]));
                        roomDTO.setPort(Integer.valueOf(splittedOutput[3]));
                    } catch (UnknownHostException ignored) {}
                    ioSharedResources.setUdpHandler(new UDPHandler(ioSharedResources, roomDTO));
                }
                System.out.println(output.equals(DEFAULT_OUTPUT_MESSAGE) ? INVALID_COMMAND_MESSAGE : output);
            }
            interrupt();
        } catch (IOException ignored) {
            interrupt();
        }
    }
}
