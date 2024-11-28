package dev.crisiswatcher.server.connection.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import dev.crisiswatcher.server.logger.Logger;
import dev.crisiswatcher.server.manager.DBManager;
import dev.crisiswatcher.server.model.MessageModel;
import dev.crisiswatcher.server.model.RoomModel;

public class UDPConnection extends Thread {
    /**
     * Socket to connect to room's group
     */
    private MulticastSocket multicastSocket;
    /**
     * Room chat
     */
    private RoomModel room;
    /**
     * Data base manager
     */
    private DBManager dbManager;

    /**
     * Constructor
     * @param room
     * @throws IOException
     * @throws UnknownHostException
     */
    public UDPConnection(RoomModel room) throws IOException, UnknownHostException{
        this.multicastSocket = new MulticastSocket(room.getPort());

        InetAddress group = InetAddress.getByName(room.getIp());
        multicastSocket.joinGroup(group);

        this.dbManager = DBManager.getInstance();
        
        Logger.addServerLogEntry("Grupo UDP criado!");
    }

    /**
     * Continues listening to the group's messages and logs them in the data base
     */
    @Override
    public void run(){
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        while(true){
            try {
                multicastSocket.receive(packet);
                MessageModel messageBD = new MessageModel();
                String message = new String(packet.getData(), 0, packet.getLength());
                messageBD.setContent(message);
                messageBD.setChatRoomId(room.getUuid());
                
                dbManager.insertMessage(messageBD);

    
            } catch (Exception e) {
                Logger.addServerLogEntry("Error: " + e.toString());
            }
        }
        
    }
}
