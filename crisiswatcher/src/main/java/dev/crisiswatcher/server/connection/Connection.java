package dev.crisiswatcher.server.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import dev.crisiswatcher.logger.Logger;
import dev.crisiswatcher.protocol.AuthenticationProtocol;
import dev.crisiswatcher.schema.User;
import dev.crisiswatcher.schema.User.UserProfile;

public class Connection extends Thread {
    private Socket clientSocket;
    private BufferedReader socketInput;
    private PrintWriter socketOutput;
    private User user;

    public Connection(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);
            user = new User();
        } catch (IOException e) {
            Logger.addServerLogEntry("Erro ao estabelecer conexão com o servidor: " + e.getMessage());
            interrupt();
        }
    }

    @Override
    public void run() {
        String input, output;
        try {
            while ((input = socketInput.readLine()) != null) {
                String finalInput = input;
                if ((output = AuthenticationProtocol.processInput(finalInput)) != null) {
                    if (user.getUuid() != 0) {
                        socketOutput.println("Já se encontra autenticado");
                        continue;
                    } else if (input.toLowerCase().contains("/login") && !output.equals("Erro na autenticação")) {
                        String[] splittedOutput = output.split(" ");
                        user.setUuid(Integer.valueOf(splittedOutput[1]));
                        user.setUsername(splittedOutput[2]);
                        user.setProfile(UserProfile.getEnum(splittedOutput[3]));
                    }

                    socketOutput.println(output);
                } else if (input.equals("/get") && user.getUuid() != 0) {
                    socketOutput.println(user);
                } else if (input.equals("/logoff") && user.getUuid() != 0) {
                    user.setUuid(0);
                    user.setUsername(null);
                    user.setProfile(null);
                    socketOutput.println("/logoff");
                } else {
                    socketOutput.println("O comando é inválido");
                }
            }
        } catch (IOException ignored) {}
    }

    public void close() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (socketInput != null) socketInput.close();
            if (socketOutput != null) socketOutput.close();
        } catch (IOException ignored) {}
    }
}
