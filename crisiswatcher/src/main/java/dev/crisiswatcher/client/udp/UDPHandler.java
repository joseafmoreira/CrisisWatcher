package dev.crisiswatcher.client.udp;

import java.io.IOException;
import java.net.MulticastSocket;

import dev.crisiswatcher.client.io.IOSharedResources;
import dev.crisiswatcher.schema.User;

public class UDPHandler extends Thread {
    private IOSharedResources ioSharedResources;
    private MulticastSocket multicastSocket;

    public UDPHandler(IOSharedResources ioSharedResources) {
        try {
            this.ioSharedResources = ioSharedResources;
            multicastSocket = new MulticastSocket();
        } catch (IOException e) {
            e.printStackTrace();
            interrupt();
        }
    }

    @Override
    public void run() {
        User user = ioSharedResources.getUser();
    }
}
