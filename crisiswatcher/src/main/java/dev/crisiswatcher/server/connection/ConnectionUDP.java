package dev.crisiswatcher.server.connection;

import dev.crisiswatcher.logger.Logger;
import dev.crisiswatcher.schema.User;

import java.net.*;

public class ConnectionUDP extends Thread {
    private MulticastSocket multicastSocket;
    private InetAddress group;
    private int port;
    private User user;

    public ConnectionUDP(MulticastSocket multicastSocket){
        this.multicastSocket = multicastSocket;
        Logger.addServerLogEntry("Grupo UDP criado!");
    }

    @Override
    public void run(){
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        //multicastSocket.receive(packet);
    }
}
