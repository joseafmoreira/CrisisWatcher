package dev.crisiswatcher.server.protocol;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dev.crisiswatcher.server.manager.Manager;
import dev.crisiswatcher.server.model.UserModel;
import dev.crisiswatcher.server.model.UserModel.UserProfile;
import dev.crisiswatcher.server.room.RoomSettingsGenerator;

public abstract class GroupChatProtocol {
    private static final List<String> profiles = new ArrayList<>();
    static {
        for (UserProfile profile : (UserProfile.values())) 
            profiles.add(profile.getValue());
    }

    public static String processInput(UserModel userModel, String input) {
        String output = null;
        if (input.startsWith("/create")) {
            String[] splittedInput = input.split(" ");
            if (splittedInput.length == 2) {
                output = createRoom(splittedInput[1]);
            }
        } else if (input.startsWith("/connect")) {
            output = connectToGroupRoom(input, userModel);
        } else if (input.startsWith("/group")) {
            String[] splittedInput = input.split(" ");
            if (splittedInput.length >= 2) {
                String message = "";
                for (int i = 1; i < splittedInput.length; i++) {
                    message += splittedInput[i].trim() + " ";
                }
                output = getChat(message.substring(0, message.length() - 1));
            }
        }
        return output;
    }

    private static String createRoom(String name) {
        String output = "Não foi possível criar a sala " + name;
        if (!profiles.contains(name)) {
            try {
                ResultSet resultSet = (Manager.getInstance()).getRoomByName(name);
                if (resultSet != null && !resultSet.next()) {
                    String generatedAddress = RoomSettingsGenerator.generateAddress();
                    String[] splittedAddress = generatedAddress.split(":");
                    String generatedCode = RoomSettingsGenerator.generateCode(name, splittedAddress[0], Integer.valueOf(splittedAddress[1]));
                    if ((Manager.getInstance()).createRoom(name, splittedAddress[0], Integer.valueOf(splittedAddress[1]), generatedCode)) 
                        output = "A sala " + name + " foi criada\nO código de entrada é: " + generatedCode;
                }
            } catch (SQLException ignored) {}
        }
        return output;
    }

    private static String connectToGroupRoom(String input, UserModel userModel) {
        String output = null; 
        String[] splittedInput = input.split(" ");
        if (splittedInput.length == 2) {
            if (profiles.contains(splittedInput[1])) {
                output = "Não possui permissões para a sala " + splittedInput[1];
                String userProfile = userModel.getProfile().getValue();
                if ((userProfile.equals("Alto")) ||
                    (userProfile.equals("Medio") && !splittedInput[1].equals("Alto")) ||
                    (userProfile.equals("Baixo") && !splittedInput[1].equals("Alto") && !splittedInput[1].equals("Medio")) ||
                    (userProfile.equals("Civil") && splittedInput[1].equals("Civil"))) {
                    try {
                        ResultSet resultSet = Manager.getInstance().getRoom(splittedInput[1]);
                        if (resultSet != null && resultSet.next()) {
                            output = "/room " + resultSet.getString(2) + " " + resultSet.getString(3) + " " + resultSet.getInt(4);
                        }
                    } catch (SQLException ignored) {}
                }
            } else {
                output = "Erro ao conectar à sala com código " + splittedInput[1];
                try {
                    ResultSet resultSet = Manager.getInstance().getRoom(splittedInput[1]);
                    if (resultSet != null && resultSet.next()) {
                        output = "/room " + resultSet.getString(2) + " " + resultSet.getString(3) + " " + resultSet.getInt(4);
                    }
                } catch (SQLException ignored) {}
            }
        }
        return output;
    }

    private static String getChat(String name) {
        return (Manager.getInstance()).getChat(name);
    }
}
