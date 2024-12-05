package dev.crisiswatcher.server.request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.ResultSet;
import java.sql.SQLException;

import dev.crisiswatcher.server.manager.Manager;
import dev.crisiswatcher.server.model.RequestModel.RequestLevel;

public class RequestHandler extends Thread {
    private static InetAddress highRoomAddress;
    private static int highRoomPort;
    private static InetAddress mediumRoomAddress;
    private static int mediumRoomPort;
    private static InetAddress lowRoomAddress;
    private static int lowRoomPort;
    static {
        try {
            ResultSet highRoomResultSet = (Manager.getInstance()).getRoom("Alto");
            ResultSet mediumRoomResultSet = (Manager.getInstance()).getRoom("Medio");
            ResultSet lowRoomResultSet = (Manager.getInstance()).getRoom("Baixo");
            if (highRoomResultSet.next() && mediumRoomResultSet.next() && lowRoomResultSet.next()) {
                highRoomAddress = InetAddress.getByName(highRoomResultSet.getString(3));
                highRoomPort = highRoomResultSet.getInt(4);
                mediumRoomAddress = InetAddress.getByName(mediumRoomResultSet.getString(3));
                mediumRoomPort = mediumRoomResultSet.getInt(4);
                lowRoomAddress = InetAddress.getByName(lowRoomResultSet.getString(3));
                lowRoomPort = lowRoomResultSet.getInt(4);
            }
        } catch (SQLException | IOException ignored) {}
    }

    @Override
    public void run() {
        while (true) {
            try {
                ResultSet requestsResultSet = (Manager.getInstance()).getRequests();
                if (requestsResultSet != null) {
                    while (requestsResultSet.next()) {
                        ResultSet userResultSet = (Manager.getInstance()).getUser(requestsResultSet.getInt(2));
                        RequestLevel requestLevel = RequestLevel.getEnum(String.valueOf(requestsResultSet.getInt(3)));
                        if (userResultSet != null && userResultSet.next()) {
                            String message = userResultSet.getString(2) + ":[Pedido] " + requestLevel.getValue() + "(" + requestsResultSet.getInt(1) + ") - approve ou deny <request_id>";
                            if (requestLevel.equals(RequestLevel.EVACUATION)) {
                                sendNotification(highRoomAddress, highRoomPort, message);
                            } else if (requestLevel.equals(RequestLevel.EMERGENCY_COMMS)) {
                                sendNotification(highRoomAddress, highRoomPort, message);
                                sendNotification(mediumRoomAddress, mediumRoomPort, message);
                            } else if (requestLevel.equals(RequestLevel.EMERGENCY_RESOURCES)) {
                                sendNotification(highRoomAddress, highRoomPort, message);
                                sendNotification(mediumRoomAddress, mediumRoomPort, message);
                                sendNotification(lowRoomAddress, lowRoomPort, message);
                            }
                        }
                    }
                }
                sleep(30000);
            } catch (SQLException | InterruptedException ignored) {}
        }
    }

    @SuppressWarnings("deprecation")
    private void sendNotification(InetAddress address, int port, String message) {
        try {
            MulticastSocket multicastSocket = new MulticastSocket(port);
            multicastSocket.joinGroup(address);
            byte[] datagramPacketBuffer = message.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(datagramPacketBuffer, datagramPacketBuffer.length, address, port);
            multicastSocket.send(datagramPacket);
            multicastSocket.close();
        } catch (IOException ignored) {}
    }
}
