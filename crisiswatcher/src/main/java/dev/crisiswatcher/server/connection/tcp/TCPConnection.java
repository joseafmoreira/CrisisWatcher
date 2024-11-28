package dev.crisiswatcher.server.connection.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import dev.crisiswatcher.server.db.Manager;
import dev.crisiswatcher.server.model.UserModel;
import dev.crisiswatcher.server.model.UserModel.UserProfile;
import dev.crisiswatcher.server.protocol.AuthenticationProtocol;
import dev.crisiswatcher.server.protocol.UserChatProtocol;
import dev.crisiswatcher.server.protocol.UserSettingsProtocol;

/**
 * Handles the TCP client connection to the server in a separate thread.
 * 
 * The available constructors for this {@code TCPConnection} include: <p>
 * <ul>
 *  <li>{@link #TCPConnection(Socket)}: Constructs a new TCPConnection thread with a specified clientSocket</li>
 * </ul>
 * 
 * The available operations for this {@code TCPConnection} include: <p>
 * <ul>
 *  <li>{@link #run()}: </li>
 * </ul>
 * 
 * <h3>TCPConnection</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 * @see Thread
 */
public class TCPConnection extends Thread {
    /**
     * Default output message of the TCP socket
     */
    private static final String DEFAULT_OUTPUT_MESSAGE = "O comando é inválido";
    /**
     * The server's TCP socket input buffered reader
     */
    private BufferedReader socketInput;
    /**
     * The server's TCP socket output print writer
     */
    private PrintWriter socketOutput;
    /**
     * The user's model
     */
    private UserModel userModel;

    /**
     * Constructs a new TCPConnection thread with a specified clientSocket.
     * 
     * @param clientSocket the specified clientSocket
     */
    public TCPConnection(Socket clientSocket) throws IOException {
        socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);
        userModel = new UserModel();
    }

    /**
     * Handles the TCP connection requests and responses
     */
    @Override
    public void run() {
        String input, output;
        try {
            while ((input = socketInput.readLine()) != null) {
                if (input.startsWith("/username") || input.startsWith("/password")) input += " " + userModel.getName();
                String finalInput = input;
                String socketOutputMessage = (input.equals("/close")) ? "/close" : DEFAULT_OUTPUT_MESSAGE;
                if (socketOutputMessage.equals(DEFAULT_OUTPUT_MESSAGE)) {
                    if (!userModel.isLogged()) {
                        if ((output = AuthenticationProtocol.processInput(finalInput)) != null) {
                            if (input.startsWith("/login") && !output.equals("Erro na autenticação")) {
                                String[] splittedOutput = output.split(" ");
                                userModel.setUuid(Integer.valueOf(splittedOutput[1]));
                                userModel.setName(splittedOutput[2]);
                                userModel.setProfile(UserProfile.getEnum(splittedOutput[3]));
                                socketOutputMessage = sendUser("Utilizador autenticado com sucesso");
                            } else {
                                socketOutputMessage = output;
                            }
                        }
                    } else {
                        if ((output = UserSettingsProtocol.processInput(finalInput)) != null) {
                            if (output.startsWith("/username")) {
                                userModel.setName(output.split(" ")[1]);
                                socketOutputMessage = sendUser("Nome de utilizador alterado com sucesso");
                            } else {
                                socketOutputMessage = output;
                            }
                        } else if ((output = UserChatProtocol.processInput(userModel, finalInput)) != null) {
                            socketOutputMessage = output;
                        } else if (input.equals("/logout")) {
                            userModel.setUuid(0);    
                            userModel.setName(null);
                            userModel.setProfile(null);
                            socketOutputMessage = sendUser("Utilizador desconectado com sucesso");
                        }
                    }
                }
                socketOutput.println(socketOutputMessage);
                if (socketOutputMessage.equals("/close")) interrupt();
            }
        } catch (IOException ignored) {}
    }

    public UserModel getUserModel() {
        return userModel;
    }

    private String sendUser(String message) {
        String name = userModel.getName();
        UserProfile profile = userModel.getProfile();

        return "/user " + 
                ((name == null) ? "null" : name) + " " + 
                ((profile == null) ? "null" : profile.getKey()) + " " + 
                message.replaceAll(" ", "_");
    }
}
