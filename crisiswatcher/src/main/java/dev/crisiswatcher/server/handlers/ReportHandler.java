package dev.crisiswatcher.server.handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.List;

import dev.crisiswatcher.server.connection.Connection;
import dev.crisiswatcher.server.logger.Logger;
import dev.crisiswatcher.server.manager.DBManager;
import dev.crisiswatcher.server.schema.Room;

public class ReportHandler extends Thread {
    
    /**
     * Server's connection list
     */
    private List<Connection> connections;

    /**
     * General Room to send the report
     */
    private Room GeneralRoom;

    /**
     * Socket to connect to general Room
     */
    private MulticastSocket multicastSocket;

    /**
     * Constructor
     * @param connections
     * @throws IOException
     */
    public ReportHandler(List<Connection> connections) throws IOException{
        this.connections = connections;
        this.GeneralRoom = DBManager.getInstance().getRoomByCode("GENERALROOM");
        this.multicastSocket = new MulticastSocket(GeneralRoom.getPort());
        InetAddress group = InetAddress.getByName(GeneralRoom.getAddress());
        multicastSocket.joinGroup(group);

        Logger.addServerLogEntry("Report Handler criado!");
    }

    /**
     * Run method to report
     */
    @Override
    public void run(){
        while (true) { 
            try {
                Thread.sleep(60000);
                reportConnections();
            } catch (Exception e) {
                Logger.addServerLogEntry("Erro no relatorio de conexoes");
            }
        }
    }

    /**
     * Auxiliar method to report the connections by multicast socket
     */
    private void reportConnections(){
        String message = "Num of Connections: " + connections.size() + "\n";
        for (Connection conn : connections) {
            message += conn.getTcpConnection().getUser().getUsername() + "\n";
        }
        byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            multicastSocket.send(packet);
        } catch (Exception e) {
            Logger.addServerLogEntry("Erro ao enviar relatorio de utilizadores conectados");
        }

    }
    
}
