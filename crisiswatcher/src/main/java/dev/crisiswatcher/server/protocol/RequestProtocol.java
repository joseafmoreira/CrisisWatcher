package dev.crisiswatcher.server.protocol;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Set;

import dev.crisiswatcher.server.manager.Manager;
import dev.crisiswatcher.server.model.RequestModel.RequestLevel;
import dev.crisiswatcher.server.model.UserModel;
import dev.crisiswatcher.server.model.UserModel.UserProfile;

public abstract class RequestProtocol {
    private static UserModel systemUserModel;
    static {
        try {
            ResultSet resultSet = (Manager.getInstance()).getSystemUser();
            if (resultSet != null && resultSet.next()) {
                systemUserModel = new UserModel();
                systemUserModel.setUuid(resultSet.getInt(1));
                systemUserModel.setName(resultSet.getString(2));
                systemUserModel.setProfile(UserProfile.getEnum(String.valueOf(resultSet.getInt(4))));
            }
        } catch (SQLException ignored) {}
    }

    public static String processInput(UserModel userModel, String input) {
        String output = null;
        if (input.startsWith("/request")) {
            output = sendRequest(input, userModel);
        } else if (input.startsWith("/approve")) {
            output = approveRequest(input, userModel);
        } else if (input.startsWith("/deny")) {
            output = denyRequest(input, userModel);
        }
        return output;
    }

    private static String sendRequest(String input, UserModel userModel) {
        String result = "Erro ao enviar o pedido";
        String[] splittedMessage = input.split(" ");
        if (splittedMessage.length == 2) {
            RequestLevel requestLevel = RequestLevel.getEnum(splittedMessage[1]);
            if (requestLevel != null) {
                if (!checkSendRequestPermissions(userModel, requestLevel)) result = "Não tem permissões para executar este pedido";
                else if ((Manager.getInstance()).sendRequest(userModel.getName(), userModel.getUuid(), requestLevel.getKey())) result = "O pedido foi enviado com sucesso";
            }
        }
        return result;
    }

    private static String approveRequest(String input, UserModel userModel) {
        String result = "Erro ao aprovar o pedido";
        String[] splittedMessage = input.split(" ");
        if (splittedMessage.length == 2) {
            try {
                ResultSet requestResultSet = (Manager.getInstance()).getRequestByID(Integer.valueOf(splittedMessage[1]));
                if (requestResultSet != null && requestResultSet.next()) {
                    if (requestResultSet.getInt(2) == userModel.getUuid()) result = "O pedido não pode ser aprovado pelo criador";
                    else if (!checkSendRequestPermissions(userModel, RequestLevel.getEnum(String.valueOf(requestResultSet.getInt(3))))) result = "Não tem permissões para aprovar este pedido";
                    else if (setRequestAnswer(Integer.valueOf(splittedMessage[1]), true)) {
                        String temp = sendNotification(Integer.valueOf(splittedMessage[1]), "O pedido foi aprovado com sucesso");
                        if (temp != null) result = temp;
                    }
                }
            } catch (SQLException ignored) {}
        }
        return result;
    }

    private static String denyRequest(String input, UserModel userModel) {
        String result = "Erro ao negar o pedido";
        String[] splittedMessage = input.split(" ");
        if (splittedMessage.length == 2) {
            try {
                ResultSet requestResultSet = (Manager.getInstance()).getRequestByID(Integer.valueOf(splittedMessage[1]));
                if (requestResultSet != null && requestResultSet.next()) {
                    if (requestResultSet.getInt(2) == userModel.getUuid()) result = "O pedido não pode ser negado pelo criador";
                    else if (!checkSendRequestPermissions(userModel, RequestLevel.getEnum(String.valueOf(requestResultSet.getInt(3))))) result = "Não tem permissões para negar este pedido";
                    else setRequestAnswer(Integer.valueOf(splittedMessage[1]), false);
                }
            } catch (SQLException ignored) {}
        }
        return result;
    }

    private static boolean checkSendRequestPermissions(UserModel userModel, RequestLevel requestLevel) {
        Set<UserProfile> allowedProfiles;
        switch (requestLevel) {
            case RequestLevel.EVACUATION:
                allowedProfiles = EnumSet.of(UserProfile.HIGH);
                break;
            case RequestLevel.EMERGENCY_COMMS:
                allowedProfiles = EnumSet.of(UserProfile.HIGH, UserProfile.MEDIUM);
                break;
            case RequestLevel.EMERGENCY_RESOURCES:
                allowedProfiles = EnumSet.of(UserProfile.HIGH, UserProfile.MEDIUM, UserProfile.LOW);
                break;
            default:
                return false;
        }
        return allowedProfiles.contains(userModel.getProfile());
    }

    private static boolean setRequestAnswer(int uuid, boolean answer) {
        return (Manager.getInstance()).setRequestAnswer(uuid, (answer) ? 1 : 0);
    }

    private static String sendNotification(int uuid, String message) {
        String output = null;
        try {
            ResultSet requestResultSet = (Manager.getInstance()).getRequestByID(Integer.valueOf(uuid));
            if (requestResultSet != null && requestResultSet.next()) {
                output = message;
                NotificationProtocol.processInput(systemUserModel, "/notification " + RequestLevel.getEnum(String.valueOf(requestResultSet.getInt(3))).getMessage());
            }
        } catch (SQLException ignored) {}
        return output;
    }
}
