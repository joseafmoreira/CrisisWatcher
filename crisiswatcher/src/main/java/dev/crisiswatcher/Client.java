package dev.crisiswatcher;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import dev.crisiswatcher.schema.User;

public class Client {
    private static final InetAddress ADDRESS = InetAddress.getLoopbackAddress();
    private static final int PORT = 27015;
    private Socket clientSocket;
    private User user;

    public Client() {
        try {
            clientSocket = new Socket(ADDRESS, PORT);
        } catch (IOException ignored) {
            System.exit(0);
        }
    }

    public void start() {
        while (true) {
            
        }
    }

    public static void main(String[] args) {
        (new Client()).start();
    }
}
