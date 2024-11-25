package dev.crisiswatcher.server.connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import dev.crisiswatcher.server.logger.Logger;
import dev.crisiswatcher.server.schema.Message;
import dev.crisiswatcher.server.schema.Room;
import dev.crisiswatcher.server.manager.DBManager;

public class ConnectionUDP extends Thread {
    private MulticastSocket multicastSocket;
    private Room room;
    private DBManager dbManager;

    public ConnectionUDP(Room room) throws IOException, UnknownHostException{
        this.multicastSocket = new MulticastSocket(room.getPort());

        InetAddress group = InetAddress.getByName(room.getAddress());
        multicastSocket.joinGroup(group);

        this.dbManager = DBManager.getInstance();
        
        Logger.addServerLogEntry("Grupo UDP criado!");
    }

    @Override
    public void run(){
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        while(true){
            try {
                multicastSocket.receive(packet);
                Message messageBD = new Message();
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
