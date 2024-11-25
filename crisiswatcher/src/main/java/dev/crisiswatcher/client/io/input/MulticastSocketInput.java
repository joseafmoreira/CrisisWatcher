package dev.crisiswatcher.client.io.input;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class MulticastSocketInput extends Thread {
    private MulticastSocket multicastSocket;

    public MulticastSocketInput(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }

    @Override
    public void run() {
        byte[] datagramPacketBuffer = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(datagramPacketBuffer, datagramPacketBuffer.length);
        while (true) {
            try {
                multicastSocket.receive(datagramPacket);
                System.out.println(new String(datagramPacket.getData()));
            } catch (SocketTimeoutException ignored) {}
            catch (IOException e) {
                interrupt();
            }
        }
    }
}
