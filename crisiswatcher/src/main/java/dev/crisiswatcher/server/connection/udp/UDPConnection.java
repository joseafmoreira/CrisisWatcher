package dev.crisiswatcher.server.connection.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import dev.crisiswatcher.server.manager.Manager;
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
    }

    @Override
    public void run() {
        while (true) {
            try {
                datagramPacketBuffer = new byte[DATAGRAM_BUFFER_SIZE];
                datagramPacket = new DatagramPacket(datagramPacketBuffer, datagramPacketBuffer.length);
                multicastSocket.receive(datagramPacket);
                String message = new String(datagramPacket.getData());
                String[] splittedMessage = message.split(":");
                (Manager.getInstance()).sendMessage(splittedMessage[0].trim(), roomName, splittedMessage[1].trim());
            } catch (IOException ignored) {}
        }
    }

    public String getRoomName() {
        return roomName;
    }
}
