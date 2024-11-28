package dev.crisiswatcher.server.handlers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import dev.crisiswatcher.server.logger.Logger;
import dev.crisiswatcher.server.manager.DBManager;
import dev.crisiswatcher.server.model.RequestModel;
import dev.crisiswatcher.server.model.RequestModel.RequestLevel;
import dev.crisiswatcher.server.model.RoomModel;

public class RequestHandler extends Thread {
    private RequestModel request;
    private RoomModel High;
    private RoomModel Medium;
    private RoomModel Low;
    private RoomModel General;
    private DBManager dbManager;
    private MulticastSocket HighSocket;
    private MulticastSocket MediumSocket;
    private MulticastSocket LowSocket;
    private MulticastSocket GeneralSocket;
    private InetAddress generalGroup;
    private InetAddress highGroup;
    private InetAddress mediumGroup;
    private InetAddress lowGroup;

    @SuppressWarnings("deprecation")
    public RequestHandler(RequestModel request) throws IOException{
        this.request = request;

        this.dbManager = DBManager.getInstance();

        this.General = dbManager.getRoomByCode("GENERALROOM");
        this.Low = dbManager.getRoomByCode("LOWROOM");
        this.Medium = dbManager.getRoomByCode("MEDIUMROOM");
        this.High = dbManager.getRoomByCode("HIGHROOM");

        this.GeneralSocket = new MulticastSocket(General.getPort());
        this.generalGroup = InetAddress.getByName(General.getIp());
        this.HighSocket = new MulticastSocket(High.getPort());
        this.highGroup = InetAddress.getByName(High.getIp());
        this.MediumSocket = new MulticastSocket(Medium.getPort());
        this.mediumGroup = InetAddress.getByName(Medium.getIp());
        this.LowSocket = new MulticastSocket(Low.getPort());
        this.lowGroup = InetAddress.getByName(Low.getIp());
        HighSocket.joinGroup(highGroup);
        MediumSocket.joinGroup(mediumGroup);
        LowSocket.joinGroup(lowGroup);
        GeneralSocket.joinGroup(generalGroup);
    } 

    @Override
    public void run(){
        if(request.getRequestLevel() == RequestLevel.COMM){
            String commString = request.getUuid() + "- REQUEST DE COMUNICACOES (/approve <request_ID>)";
            try {
                sendMessage(commString, HighSocket,High.getPort() , highGroup );
                sendMessage(commString, MediumSocket,Medium.getPort() , mediumGroup );

            } catch (Exception e) {
                Logger.addServerLogEntry("Error Request: "+e.getMessage());
            }

        }else if(request.getRequestLevel() == RequestLevel.EVAC){
            String evacString = request.getUuid() + "- REQUEST DE EVACUAÇÃO (/approve <request_ID>)";
            try {
                sendMessage(evacString, HighSocket,High.getPort() , highGroup );

            } catch (Exception e) {
                Logger.addServerLogEntry("Error Request: "+e.getMessage());
            }
            
        }else if(request.getRequestLevel() == RequestLevel.RES){
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
                sendMessage(request.getRequestLevel().toString(), GeneralSocket, General.getPort(), generalGroup);
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
