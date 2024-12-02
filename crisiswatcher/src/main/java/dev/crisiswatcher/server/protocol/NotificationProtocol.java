package dev.crisiswatcher.server.protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;

import dev.crisiswatcher.server.manager.Manager;
import dev.crisiswatcher.server.model.UserModel;

public abstract class NotificationProtocol {
    public static String processInput(String input, UserModel userModel) {
        String output = null;
        if (input.startsWith("/notification")) {
            output = sendNotification(input, userModel);
        }
        return output;
    }

    private static String sendNotification(String input, UserModel userModel) {
        String output = "Erro ao enviar a notifcação";
        String[] splittedMessage = input.split(" ");
        if (splittedMessage.length >= 2 && (userModel.getProfile().getValue().equals("Alto") || userModel.getProfile().getValue().equals("Medio"))) {
            if (userModel.getProfile().getValue().equals("Alto") || userModel.getProfile().getValue().equals("Medio")) {
                String message = "";
                for (int i = 1; i < splittedMessage.length; i++) 
                    message += splittedMessage[i] + " ";
                message = message.substring(0, message.length() - 1);
                try {
                    ResultSet roomsResultSet = (Manager.getInstance()).getRooms();
                    if (roomsResultSet != null) {
                        while (roomsResultSet.next()) {
                            try {
                                sendToGroup(roomsResultSet.getString(3), roomsResultSet.getInt(4), userModel.getName(), message);
                            } catch (IOException ignored) {}
                        }
                    }
                    (Manager.getInstance()).sendNotification(userModel.getName(), userModel.getUuid(), message);
                    output = "Notificação enviada com sucesso";
                } catch (SQLException ignored) {}
            } else {
                output = "Não tem permissões para aceder a este comando";
            }      
        }
        return output;
    }

    @SuppressWarnings("deprecation")
    private static void sendToGroup(String address, int port, String username, String message) throws IOException, UnknownHostException {
        MulticastSocket multicastSocket = new MulticastSocket(port);
        multicastSocket.joinGroup(InetAddress.getByName(address));
        byte[] datagramPacketBuffer = (username + ": " + message).getBytes(StandardCharsets.UTF_8);
        multicastSocket.send(new DatagramPacket(datagramPacketBuffer, datagramPacketBuffer.length, InetAddress.getByName(address), port));
        multicastSocket.close();
    }
}
