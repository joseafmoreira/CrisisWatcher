package dev.crisiswatcher.server.request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.ResultSet;
import java.sql.SQLException;

import dev.crisiswatcher.server.manager.Manager;
import dev.crisiswatcher.server.model.RequestModel.RequestLevel;

@SuppressWarnings("deprecation")
public class RequestHandler extends Thread {
    private static MulticastSocket highRoomSocket;
    private static MulticastSocket mediumRoomSocket;
    private static MulticastSocket lowRoomSocket;
    private static MulticastSocket civilianRoomSocket;
    private static InetAddress highRoomAddress;
    private static int highRoomPort;
    private static InetAddress mediumRoomAddress;
    private static int mediumRoomPort;
    private static InetAddress lowRoomAddress;
    private static int lowRoomPort;
    private static InetAddress civilianRoomAddress;
    private static int civilianRoomPort;
    static {
        try {
            ResultSet highRoomResultSet = (Manager.getInstance()).getRoom("Alto");
            ResultSet mediumRoomResultSet = (Manager.getInstance()).getRoom("Medio");
            ResultSet lowRoomResultSet = (Manager.getInstance()).getRoom("Baixo");
            ResultSet civilianRoomResultSet = (Manager.getInstance()).getRoom("Civil");
            if (highRoomResultSet.next() && mediumRoomResultSet.next() && lowRoomResultSet.next() && civilianRoomResultSet.next()) {
                highRoomAddress = InetAddress.getByName(highRoomResultSet.getString(3));
                highRoomPort = highRoomResultSet.getInt(4);
                mediumRoomAddress = InetAddress.getByName(mediumRoomResultSet.getString(3));
                mediumRoomPort = mediumRoomResultSet.getInt(4);
                lowRoomAddress = InetAddress.getByName(lowRoomResultSet.getString(3));
                lowRoomPort = lowRoomResultSet.getInt(4);
                civilianRoomAddress = InetAddress.getByName(civilianRoomResultSet.getString(3));
                civilianRoomPort = civilianRoomResultSet.getInt(4);
                highRoomSocket = new MulticastSocket(highRoomPort);
                highRoomSocket.joinGroup(highRoomAddress);
                mediumRoomSocket = new MulticastSocket(mediumRoomPort);
                mediumRoomSocket.joinGroup(mediumRoomAddress);
                lowRoomSocket = new MulticastSocket(lowRoomPort);
                lowRoomSocket.joinGroup(lowRoomAddress);
                civilianRoomSocket = new MulticastSocket(civilianRoomPort);
                civilianRoomSocket.joinGroup(civilianRoomAddress);
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
                                sendNotification(highRoomSocket, highRoomAddress, highRoomPort, message);
                            } else if (requestLevel.equals(RequestLevel.EMERGENCY_COMMS)) {
                                sendNotification(highRoomSocket, highRoomAddress, highRoomPort, message);
                                sendNotification(mediumRoomSocket, mediumRoomAddress, mediumRoomPort, message);
                            } else if (requestLevel.equals(RequestLevel.EMERGENCY_RESOURCES)) {
                                sendNotification(highRoomSocket, highRoomAddress, highRoomPort, message);
                                sendNotification(mediumRoomSocket, mediumRoomAddress, mediumRoomPort, message);
                                sendNotification(lowRoomSocket, lowRoomAddress, lowRoomPort, message);
                            }
                        }
                    }
                }
                sleep(30000);
            } catch (SQLException | InterruptedException ignored) {}
        }
    }

    private void sendNotification(MulticastSocket multicastSocket, InetAddress address, int port, String message) {
        try {
            byte[] datagramPacketBuffer = message.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(datagramPacketBuffer, datagramPacketBuffer.length, address, port);
            multicastSocket.send(datagramPacket);
        } catch (IOException ignored) {}
    }
}
