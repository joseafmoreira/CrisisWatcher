package dev.crisiswatcher;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import dev.crisiswatcher.client.io.IOSharedResources;
import dev.crisiswatcher.client.io.input.SocketInput;
import dev.crisiswatcher.client.io.input.StandardInput;
import dev.crisiswatcher.client.io.output.SocketOutput;
import dev.crisiswatcher.schema.User;

public class Client {
    private static final InetAddress ADDRESS = InetAddress.getLoopbackAddress();
    private static final int PORT = 27015;
    private Socket clientSocket;
    private IOSharedResources ioSharedResources;
    private SocketInput socketInput;
    private StandardInput standardInput;
    private SocketOutput socketOutput;
    private User user;

    public Client() {
        try {
            clientSocket = new Socket(ADDRESS, PORT);
            ioSharedResources = new IOSharedResources();
            socketInput = new SocketInput(ioSharedResources, clientSocket);
            standardInput = new StandardInput(ioSharedResources);
            socketOutput = new SocketOutput(ioSharedResources, clientSocket);
        } catch (IOException ignored) {
            System.exit(0);
        }
    }

    public void start() {
        socketInput.start();
        standardInput.start();
        socketOutput.start();
        while (true) 
            if (socketOutput.isInterrupted() || socketInput.isInterrupted()) System.exit(0);
    }

    public User getUser() {
        return user;
    }

    public static void main(String[] args) {
        (new Client()).start();
    }
}
