package dev.crisiswatcher.server.handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import dev.crisiswatcher.server.connection.tcp.TCPConnection;
import dev.crisiswatcher.server.file.FileHandler;
import dev.crisiswatcher.server.logger.Logger;
import dev.crisiswatcher.server.manager.DBManager;
import dev.crisiswatcher.server.model.RoomModel;

public class ReportHandler extends Thread {
    
    /**
     * Server's connection list
     */
    private List<TCPConnection> connections;

    /**
     * General Room to send the report
     */
    private RoomModel GeneralRoom;

    /**
     * Socket to connect to general Room
     */
    private MulticastSocket multicastSocket;

    /**
     * Constructor
     * @param connections
     * @throws IOException
     */
    public ReportHandler(List<TCPConnection> connections) throws IOException{
        this.connections = connections;
        this.GeneralRoom = DBManager.getInstance().getRoomByCode("GENERALROOM");
        this.multicastSocket = new MulticastSocket(GeneralRoom.getPort());
        InetAddress group = InetAddress.getByName(GeneralRoom.getIp());
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
                reportActions();
            } catch (Exception e) {
                Logger.addServerLogEntry("Erro no relatorio de conexoes");
            }
        }
    }

    /**
     * Auxiliary method to report the connections by multicast socket
     */
    private void reportConnections(){
        String message = "Num of Connections: " + connections.size() + "\n";
        for (TCPConnection conn : connections) {
            message += conn.getUser().getName() + "\n";
        }
        byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            multicastSocket.send(packet);
        } catch (Exception e) {
            Logger.addServerLogEntry("Erro ao enviar relatorio de utilizadores conectados");
        }

    }

    private void reportActions(){
        String message = "Ultimas 10 acoes realizadas: \n";
        
        List<String> logLines = new ArrayList<>(FileHandler.readFile("logs/server.log"));

        
        for (int i = 0 ; i < 10 ; i++) {
            try {
                message += logLines.get(i) + "\n";
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            multicastSocket.send(packet);
        } catch (Exception e) {
            Logger.addServerLogEntry("Erro ao enviar relatorio de acoes");
        }

    }
    
}
