package dev.joseafmoreira.db.server.socketconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import dev.joseafmoreira.db.server.protocol.AuthenticationProtocol;
import dev.joseafmoreira.log.LogHandler;

public class SocketConnection extends Thread implements AutoCloseable {
    private Socket clientSocket;
    private BufferedReader socketInput;
    private PrintWriter socketOutput;

    public SocketConnection(Socket clientSocket) {
        try {
            this.clientSocket = clientSocket;
            socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);
            LogHandler.addDBLogEntry("O utilizador " + clientSocket.getInetAddress().toString().split("/")[1] + ":" + clientSocket.getLocalPort() + " conectou-se ao servidor DB");
        } catch (IOException ignored) {
            LogHandler.addDBLogEntry("O utilizador " + clientSocket.getInetAddress().toString().split("/")[1] + ":" + clientSocket.getLocalPort() + " falhou ao conectar ao servidor DB");
            interrupt();
        }
    }

    @Override
    public void run() {
        String input;
        try {
            while ((input = socketInput.readLine()) != null) {
                String finalInput = input;
                new Thread(() -> {
                    String output = null;
                    if ((output = AuthenticationProtocol.processInput(finalInput)) != null) {
                        socketOutput.println(output);
                    } else {
                        socketOutput.println("O comando introduzido não é válido");
                    }
                }).start();
            }
        } catch (IOException ignored) {}
    }

    @Override
    public void close() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (socketInput != null) socketInput.close();
            if (socketOutput != null) socketOutput.close();
        } catch (IOException ignored) {}
    }
}
