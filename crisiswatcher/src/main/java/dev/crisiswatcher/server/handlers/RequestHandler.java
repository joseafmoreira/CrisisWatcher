package dev.crisiswatcher.server.handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import dev.crisiswatcher.server.logger.Logger;
import dev.crisiswatcher.server.schema.Request;
import dev.crisiswatcher.server.schema.Request.RequestLevel;
import dev.crisiswatcher.server.manager.DBManager;
import dev.crisiswatcher.server.schema.Room;

public class RequestHandler extends Thread {
    private Request request;
    private Room High;
    private Room Medium;
    private Room Low;
    private Room General;
    private DBManager dbManager;
    private MulticastSocket HighSocket;
    private MulticastSocket MediumSocket;
    private MulticastSocket LowSocket;
    private MulticastSocket GeneralSocket;
    private InetAddress generalGroup;
    private InetAddress highGroup;
    private InetAddress mediumGroup;
    private InetAddress lowGroup;

    public RequestHandler(Request request) throws IOException{
        this.request = request;

        this.dbManager = DBManager.getInstance();

        this.General = dbManager.getRoomByCode("GENERALROOM");
        this.Low = dbManager.getRoomByCode("LOWROOM");
        this.Medium = dbManager.getRoomByCode("MEDIUMROOM");
        this.High = dbManager.getRoomByCode("HIGHROOM");

        this.GeneralSocket = new MulticastSocket(General.getPort());
        this.generalGroup = InetAddress.getByName(General.getAddress());
        this.HighSocket = new MulticastSocket(High.getPort());
        this.highGroup = InetAddress.getByName(High.getAddress());
        this.MediumSocket = new MulticastSocket(Medium.getPort());
        this.mediumGroup = InetAddress.getByName(Medium.getAddress());
        this.LowSocket = new MulticastSocket(Low.getPort());
        this.lowGroup = InetAddress.getByName(Low.getAddress());
        HighSocket.joinGroup(highGroup);
        MediumSocket.joinGroup(mediumGroup);
        LowSocket.joinGroup(lowGroup);
        GeneralSocket.joinGroup(generalGroup);
    } 

    @Override
    public void run(){
        if(request.getRequest() == RequestLevel.COMM){
            String commString = request.getUuid() + "- REQUEST DE COMUNICACOES (/approve <request_ID>)";
            try {
                sendMessage(commString, HighSocket,High.getPort() , highGroup );
                sendMessage(commString, MediumSocket,Medium.getPort() , mediumGroup );

            } catch (Exception e) {
                Logger.addServerLogEntry("Error Request: "+e.getMessage());
            }

        }else if(request.getRequest() == RequestLevel.EVAC){
            String evacString = request.getUuid() + "- REQUEST DE EVACUAÇÃO (/approve <request_ID>)";
            try {
                sendMessage(evacString, HighSocket,High.getPort() , highGroup );

            } catch (Exception e) {
                Logger.addServerLogEntry("Error Request: "+e.getMessage());
            }
            
        }else if(request.getRequest() == RequestLevel.RES){
            String resString = request.getUuid() + "- REQUEST DE RECURSOS (/approve <request_ID>)";
            try {
                sendMessage(resString, HighSocket,High.getPort() , highGroup );
                sendMessage(resString, MediumSocket,Medium.getPort() , mediumGroup );
                sendMessage(resString, LowSocket,Low.getPort() , lowGroup );

            } catch (Exception e) {
                Logger.addServerLogEntry("Error Request: "+e.getMessage());
            }

        }
        while (request.isApproved() == null) {}
        if(request.isApproved()){
            try {
                sendMessage(request.getRequest().toString(), GeneralSocket, General.getPort(), generalGroup);
            } catch (Exception e) {
                Logger.addServerLogEntry("Erro a enviar REQUEST NOTIFICATION: "+e.getMessage());
            }
            
        }else {
            Logger.addServerLogEntry("Request Negado");
        }
    
    }

    public void sendMessage(String message, MulticastSocket socket, int port, InetAddress group) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
        socket.send(packet);
        Logger.addServerLogEntry("Message sent: " + message);
    }
}
