package dev.crisiswatcher.client.io.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import dev.crisiswatcher.client.io.IOSharedResources;
import dev.crisiswatcher.schema.User;
import dev.crisiswatcher.schema.User.UserProfile;

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
        User user = ioSharedResources.getUser();
        String output;
        try {
            while ((output = socketInput.readLine()) != null) {
                if (output.equals("/close")) {
                    System.out.println("Até à próxima");
                    break;
                } else if (output.startsWith("/user")) {
                    String[] splittedOutput = output.split(" ");
                    user.setUuid(Integer.valueOf(splittedOutput[1]));
                    user.setUsername((splittedOutput[2].equals("null")) ? null : splittedOutput[2]);
                    user.setProfile((splittedOutput[3].equals("null")) ? null : UserProfile.getEnum(splittedOutput[3]));
                    output = splittedOutput[4].replaceAll("_", " ");
                } else if (output.startsWith("/room")) {
                    
                }
                System.out.println(output.equals(DEFAULT_OUTPUT_MESSAGE) ? INVALID_COMMAND_MESSAGE : output);
            }
            interrupt();
        } catch (IOException ignored) {
            interrupt();
        }
    }
}
