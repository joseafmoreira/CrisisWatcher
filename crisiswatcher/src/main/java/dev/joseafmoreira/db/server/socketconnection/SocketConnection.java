package dev.joseafmoreira.db.server.socketconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import dev.joseafmoreira.db.server.manager.Manager;

public class SocketConnection extends Thread {
    private Socket clientSocket;
    private Manager manager;
    private BufferedReader socketInput;
    private PrintWriter socketOutput;

    public SocketConnection(Socket clientSocket) {
        this.clientSocket = clientSocket;
        manager = Manager.getInstance();
        try {
            socketInput = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException ignored) {
            interrupt();
        }
    }

    @Override
    public void run() {
        String input;
        try {
            while ((input = socketInput.readLine()) != null) {
                
            }
        } catch (IOException ignored) {}
    }

    public void close() {
        try {
            if (socketInput != null) socketInput.close();
            if (socketOutput != null) socketOutput.close();
        } catch (IOException ignored) {}
    }
}
