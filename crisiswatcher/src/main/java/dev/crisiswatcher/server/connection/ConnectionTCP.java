package dev.crisiswatcher.server.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import dev.crisiswatcher.logger.Logger;
import dev.crisiswatcher.protocol.AuthenticationProtocol;
import dev.crisiswatcher.protocol.RoomProtocol;
import dev.crisiswatcher.protocol.UserSettingsProtocol;
import dev.crisiswatcher.schema.Room;
import dev.crisiswatcher.schema.User;
import dev.crisiswatcher.schema.User.UserProfile;
import dev.crisiswatcher.server.manager.DBManager;

public class ConnectionTCP extends Thread {
    private static final String DEFAULT_OUTPUT_MESSAGE = "O comando é inválido";
    private Socket clientSocket;
    private BufferedReader socketInput;
    private PrintWriter socketOutput;
    private User user;
    private DBManager dbManager;

    public ConnectionTCP(Socket clientSocket) {
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
                if (input.startsWith("/username") || input.startsWith("/password")) input += " " + user.getUsername();
                String finalInput = input;
                String socketOutputMessage = (input.equals("/close")) ? "/close" : DEFAULT_OUTPUT_MESSAGE;
                if (socketOutputMessage.equals(DEFAULT_OUTPUT_MESSAGE)) {
                    if (!user.isLogged()) {
                        if ((output = AuthenticationProtocol.processInput(finalInput)) != null) {
                            if (input.startsWith("/login") && !output.equals("Erro na autenticação")) {
                                String[] splittedOutput = output.split(" ");
                                user.setUuid(Integer.valueOf(splittedOutput[1]));
                                user.setUsername(splittedOutput[2]);
                                user.setProfile(UserProfile.getEnum(splittedOutput[3]));
                                socketOutputMessage = sendUser("Utilizador autenticado com sucesso");
                            } else {
                                socketOutputMessage = output;
                            }
                        }
                    } else {
                        if ((output = UserSettingsProtocol.processInput(finalInput)) != null) {
                            if (output.startsWith("/username")) {
                                user.setUsername(output.split(" ")[1]);
                                socketOutputMessage = sendUser("Nome de utilizador alterado com sucesso");
                            } else {
                                socketOutputMessage = output;
                            }
                        } else if (input.equals("/logout")) {
                            user.setUuid(0);    
                            user.setUsername(null);
                            user.setProfile(null);
                            socketOutputMessage = sendUser("Utilizador desconectado com sucesso");
                        } else if ( input.startsWith("/createRoom")){
                            String[] splitterInput = input.split(" ");
                            Room room = new Room();
                            room.setName(splitterInput[1]);
                            room.setCode(RoomProtocol.generateCode());
                            room.setAddress(RoomProtocol.getIp());
                            room.setPort(6789);
                            dbManager.insertRoom(room);
                            new ConnectionUDP(room).start();
                        }else if( input.startsWith("/request")){
                            

                        }
                    }
                }
                socketOutput.println(socketOutputMessage);
                if (socketOutputMessage.equals("/close")) interrupt();
            }
        } catch (IOException ignored) {}
    }

    public User getUser(){
        return user;
    }

    public void close() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (socketInput != null) socketInput.close();
            if (socketOutput != null) socketOutput.close();
        } catch (IOException ignored) {}
    }

    private String sendUser(String message) {
        String name = user.getUsername();
        UserProfile profile = user.getProfile();

        return "/user " + 
                user.getUuid() + " " + 
                ((name == null) ? "null" : name) + " " + 
                ((profile == null) ? "null" : profile.getKey()) + " " + 
                message.replaceAll(" ", "_");
    }
}
