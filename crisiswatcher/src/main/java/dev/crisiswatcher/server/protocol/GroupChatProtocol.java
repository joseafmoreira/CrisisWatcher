package dev.crisiswatcher.server.protocol;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dev.crisiswatcher.server.manager.Manager;
import dev.crisiswatcher.server.model.UserModel;
import dev.crisiswatcher.server.model.UserModel.UserProfile;

public abstract class GroupChatProtocol {
    private static final List<String> profiles = new ArrayList<>();
    static {
        for (UserProfile profile : (UserProfile.values())) 
            profiles.add(profile.getValue());
    }

    public static String processInput(UserModel userModel, String input) {
        String output = null;
        if (input.startsWith("/connect")) {
            output = connectToGroupRoom(input, userModel);
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
}
