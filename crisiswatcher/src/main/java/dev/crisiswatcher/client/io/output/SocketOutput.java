package dev.crisiswatcher.client.io.output;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import dev.crisiswatcher.client.io.IOSharedResources;

public class SocketOutput extends Thread {
    private IOSharedResources ioSharedResources;
    private PrintWriter socketOutput;

    public SocketOutput(IOSharedResources ioSharedResources, Socket clientSocket) throws IOException {
        this.ioSharedResources = ioSharedResources;
        socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        String output;
        while (true) {
            if (!ioSharedResources.getOutputBuffer().isEmpty()) {
                output = ioSharedResources.getOutputBuffer().removeFirst();
                socketOutput.println(output);
            }
        }
    }
}
