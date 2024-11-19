package dev.crisiswatcher.client.io.output;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import dev.crisiswatcher.client.io.IOSharedResources;

public class SocketOutput extends Thread {
    private IOSharedResources ioSharedResources;
    private PrintWriter socketOutput;

    public SocketOutput(IOSharedResources ioSharedResources, OutputStream socketOutputStream) {
        this.ioSharedResources = ioSharedResources;
        socketOutput = new PrintWriter(socketOutputStream, true);
    }

    @Override
    public void run() {
        List<String> tcpOutputBuffer = ioSharedResources.getTcpOutputBuffer();
        while (true) 
            if (!tcpOutputBuffer.isEmpty()) 
                socketOutput.println(tcpOutputBuffer.removeFirst());
    }
}
