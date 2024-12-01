package dev.crisiswatcher.server.connection.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import dev.crisiswatcher.server.model.RoomModel;

public class UDPConnection extends Thread {
    private static final int DATAGRAM_BUFFER_SIZE = 1024;
    private String roomName;
    private MulticastSocket multicastSocket;
    private byte[] datagramPacketBuffer;
    private DatagramPacket datagramPacket;
    
    @SuppressWarnings("deprecation")
    public UDPConnection(RoomModel roomModel) throws IOException {
        roomName = roomModel.getName();
        multicastSocket = new MulticastSocket(roomModel.getPort());
        multicastSocket.joinGroup(roomModel.getIp());
        datagramPacketBuffer = new byte[DATAGRAM_BUFFER_SIZE];
        datagramPacket = new DatagramPacket(datagramPacketBuffer, datagramPacketBuffer.length);
    }

    @Override
    public void run() {
        while (true) {
            try {
                multicastSocket.receive(datagramPacket);
                System.out.println("[" + roomName + "] -> " + new String(datagramPacket.getData()));
            } catch (IOException ignored) {}
        }
    }

    public String getRoomName() {
        return roomName;
    }
}
