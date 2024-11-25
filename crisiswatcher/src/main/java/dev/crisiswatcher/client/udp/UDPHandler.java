package dev.crisiswatcher.client.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

import dev.crisiswatcher.client.dto.RoomDTO;
import dev.crisiswatcher.client.io.IOSharedResources;
import dev.crisiswatcher.client.io.input.MulticastSocketInput;
import dev.crisiswatcher.client.io.output.MulticastSocketOutput;

public class UDPHandler extends Thread {
    private static final int SO_TIMEOUT= 1000;
    private MulticastSocket multicastSocket;
    private MulticastSocketInput multicastSocketInput;
    private MulticastSocketOutput multicastSocketOutput;

    @SuppressWarnings("deprecation")
    public UDPHandler(IOSharedResources ioSharedResources, RoomDTO roomDTO) {
        if (roomDTO.getAddress() != null) {
            try {
                multicastSocket = new MulticastSocket(roomDTO.getPort());
                multicastSocket.joinGroup(roomDTO.getAddress());
                multicastSocket.setSoTimeout(SO_TIMEOUT);
                multicastSocketInput = new MulticastSocketInput(multicastSocket);
                multicastSocketOutput = new MulticastSocketOutput(ioSharedResources, multicastSocket);
                start();
            } catch (IOException e) {
                e.printStackTrace();
                interrupt();
            }
        } else {
            interrupt();
        }
    }

    @Override
    public void run() {
        multicastSocketInput.start();
        multicastSocketOutput.start();
        while (true) 
            if (multicastSocketInput.isInterrupted() || multicastSocketOutput.isInterrupted()) break;
        interrupt();
    }
}
