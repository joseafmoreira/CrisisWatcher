package dev.crisiswatcher.client.tcp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import dev.crisiswatcher.client.io.IOSharedResources;
import dev.crisiswatcher.client.io.input.SocketInput;
import dev.crisiswatcher.client.io.output.SocketOutput;

public class TCPHandler extends Thread {
    private static final InetAddress ADDRESS = InetAddress.getLoopbackAddress();
    private static final int PORT = 27015;
    private Socket tcpSocket;
    private SocketInput socketInput;
    private SocketOutput socketOutput;

    public TCPHandler(IOSharedResources ioSharedResources) {
        try {
            tcpSocket = new Socket(ADDRESS, PORT);
            socketInput = new SocketInput(ioSharedResources, tcpSocket.getInputStream());
            socketOutput = new SocketOutput(ioSharedResources, tcpSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            interrupt();
        }
    }

    @Override
    public void run() {
        if (socketInput != null && socketOutput != null) {
            socketInput.start();
            socketOutput.start();
            while (true) 
                if (socketInput.isInterrupted() || socketOutput.isInterrupted()) interrupt();
        }
    }
}
