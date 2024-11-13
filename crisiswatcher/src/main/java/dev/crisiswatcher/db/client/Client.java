package dev.crisiswatcher.db.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private static final InetAddress ADDRESS = InetAddress.getLoopbackAddress();
    private static final int PORT = 1433;
    private Socket clientSocket;
    private BufferedReader socketInput;
    private BufferedReader inInput;
    private PrintWriter socketOutput;

    public Client() {
        try {
            clientSocket = new Socket(ADDRESS, PORT);
            socketInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            inInput = new BufferedReader(new InputStreamReader(System.in));
            socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException ignored) {
            System.exit(0);
        }
    }

    public void start() {
        String input, sInput;
        try {
            while ((input = inInput.readLine()) != null) {
                socketOutput.println(input);
                sInput = socketInput.readLine();
                if (sInput.equals("/close")) break;
                System.out.println(sInput);
            }
        } catch (IOException ignored) {}
    }
}
