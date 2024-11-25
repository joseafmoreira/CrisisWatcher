package dev.crisiswatcher.client.io.output;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import dev.crisiswatcher.client.io.IOSharedResources;

public class MulticastSocketOutput extends Thread {
    private IOSharedResources ioSharedResources;
    private MulticastSocket multicastSocket;

    public MulticastSocketOutput(IOSharedResources ioSharedResources, MulticastSocket multicastSocket) {
        this.ioSharedResources = ioSharedResources;
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {
        List<String> udpOutputBuffer = ioSharedResources.getUdpOutputBuffer();
        while (true) {
            if (!udpOutputBuffer.isEmpty()) {
                String message = udpOutputBuffer.removeFirst();
                byte[] datagramPacketBuffer = message.getBytes(StandardCharsets.UTF_8);
                DatagramPacket datagramPacket = new DatagramPacket(datagramPacketBuffer, datagramPacketBuffer.length);
                try {
                    multicastSocket.send(datagramPacket);
                } catch (SocketTimeoutException ignored) {}
                catch (IOException e) {
                    interrupt();
                }
            }
        }
    }
}
