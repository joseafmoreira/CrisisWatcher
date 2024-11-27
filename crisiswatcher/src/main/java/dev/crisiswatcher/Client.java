package dev.crisiswatcher;

import dev.crisiswatcher.client.handler.TCPHandler;
import dev.crisiswatcher.client.io.IOSharedResources;
import dev.crisiswatcher.client.io.input.StandardInput;

public class Client {
    private IOSharedResources ioSharedResources;
    private StandardInput standardInput;
    private TCPHandler tcpHandler;

    private Client() {
        ioSharedResources = new IOSharedResources();
        standardInput = new StandardInput(ioSharedResources);
    }

    public void start() {
        standardInput.start();
    }

    public static void main(String[] args) {
        (new Client()).start();
    }
}
