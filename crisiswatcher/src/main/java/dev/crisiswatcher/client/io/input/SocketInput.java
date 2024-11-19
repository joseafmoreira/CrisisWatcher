package dev.crisiswatcher.client.io.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import dev.crisiswatcher.client.io.IOSharedResources;
import dev.crisiswatcher.schema.User;
import dev.crisiswatcher.schema.User.UserProfile;

public class SocketInput extends Thread {
    private IOSharedResources ioSharedResources;
    private BufferedReader socketInput;

    public SocketInput(IOSharedResources ioSharedResources, Socket clienSocket) throws IOException {
        this.ioSharedResources = ioSharedResources;
        socketInput = new BufferedReader(new InputStreamReader(clienSocket.getInputStream()));
    }

    @Override
    public void run() {
        User user = ioSharedResources.getUser();
        String output;
        try {
            while ((output = socketInput.readLine()) != null) {
                if (output.contains("/login")) {
                    String[] splittedOutput = output.split(" ");
                    user.setUuid(Integer.valueOf(splittedOutput[1]));
                    user.setUsername(splittedOutput[2]);
                    user.setProfile(UserProfile.getEnum(splittedOutput[3]));
                    output = "Login efetuado com sucesso";
                } else if (output.contains("/username")) {
                    user.setUsername(output.split(" ")[1]);
                    output = "Nome de utilizador alterado com sucesso";
                } else if (output.contains("/get")) {
                    System.out.println(user);
                } else if (output.equals("/logoff")) {
                    user.setUuid(0);
                    user.setUsername(null);
                    user.setProfile(null);
                    output = "O utilizador foi desconectado com sucesso";
                }

                if (output.equals("/close")) interrupt(); 
                else System.out.println(output);
            }
        } catch (IOException ignored) {
            interrupt();
        }
    }
}
