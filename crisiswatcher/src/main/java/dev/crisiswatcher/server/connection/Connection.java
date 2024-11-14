package dev.crisiswatcher.server.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import dev.crisiswatcher.logger.Logger;
import dev.crisiswatcher.schema.User;

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
        } catch (IOException e) {
            Logger.addServerLogEntry("Erro ao estabelecer conexão com o servidor: " + e.getMessage());
            interrupt();
        }
    }

    @Override
    public void run() {
        String input;
        while (true) {
            
        }
    }

    public void close() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (socketInput != null) socketInput.close();
            if (socketOutput != null) socketOutput.close();
        } catch (IOException ignored) {}
    }
}
