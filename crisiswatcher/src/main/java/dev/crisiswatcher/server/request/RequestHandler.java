package dev.crisiswatcher.server.request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.ResultSet;

import dev.crisiswatcher.server.logger.Logger;
import dev.crisiswatcher.server.manager.Manager;
import dev.crisiswatcher.server.model.RequestModel;
import dev.crisiswatcher.server.model.RequestModel.RequestLevel;

public class RequestHandler extends Thread {
    private RequestModel request;
    private ResultSet High;
    private ResultSet Medium;
    private ResultSet Low;
    private ResultSet General;
    private Manager dbManager;
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

        this.dbManager = Manager.getInstance();

        this.General = dbManager.getRoomByName("Civil");
        this.Low = dbManager.getRoomByName("Baixo");
        this.Medium = dbManager.getRoomByName("Medio");
        this.High = dbManager.getRoomByName("Alto");

        try {
            this.GeneralSocket = new MulticastSocket(General.getInt(4));
            this.generalGroup = InetAddress.getByName(General.getString(3));
            this.HighSocket = new MulticastSocket(High.getInt(4));
            this.highGroup = InetAddress.getByName(High.getString(3));
            this.MediumSocket = new MulticastSocket(Medium.getInt(4));
            this.mediumGroup = InetAddress.getByName(Medium.getString(3));
            this.LowSocket = new MulticastSocket(Low.getInt(4));
            this.lowGroup = InetAddress.getByName(Low.getString(3));
        } catch (Exception e) {
            //TODO LOGGER SHIT
        }
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
                sendMessage(commString, HighSocket,High.getInt(4) , highGroup );
                sendMessage(commString, MediumSocket,Medium.getInt(4) , mediumGroup );

            } catch (Exception e) {
                Logger.addServerLogEntry("Error Request: "+e.getMessage());
            }

        }else if(request.getRequestLevel() == RequestLevel.EVAC){
            String evacString = request.getUuid() + "- REQUEST DE EVACUAÇÃO (/approve <request_ID>)";
            try {
                sendMessage(evacString, HighSocket,High.getInt(4) , highGroup );

            } catch (Exception e) {
                Logger.addServerLogEntry("Error Request: "+e.getMessage());
            }
            
        }else if(request.getRequestLevel() == RequestLevel.RES){
            String resString = request.getUuid() + "- REQUEST DE RECURSOS (/approve <request_ID>)";
            try {
                sendMessage(resString, HighSocket,High.getInt(4) , highGroup );
                sendMessage(resString, MediumSocket,Medium.getInt(4) , mediumGroup );
                sendMessage(resString, LowSocket,Low.getInt(4) , lowGroup );

            } catch (Exception e) {
                Logger.addServerLogEntry("Error Request: "+e.getMessage());
            }

        }
        while (request.isApproved() == null) {}
        if(request.isApproved()){
            try {
                sendMessage(request.getRequestLevel().toString(), GeneralSocket, General.getInt(4), generalGroup);
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
