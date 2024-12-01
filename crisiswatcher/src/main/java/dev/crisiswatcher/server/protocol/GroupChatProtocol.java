package dev.crisiswatcher.server.protocol;

import java.sql.ResultSet;
import java.sql.SQLException;

import dev.crisiswatcher.server.manager.Manager;

public abstract class GroupChatProtocol {
    public static String processInput(String input) {
        String output = null;
        if (input.startsWith("/connect")) {
            output = connectToGroupRoom(input);
        }
        return output;
    }

    private static String connectToGroupRoom(String input) {
        String output = null; 
        String[] splittedInput = input.split(" ");
        if (splittedInput.length == 2) {
            output = "Erro ao conectar à sala com código " + splittedInput[1];
            try {
                ResultSet resultSet = Manager.getInstance().getRoom(splittedInput[1]);
                if (resultSet != null && resultSet.next()) 
                    output = "/room " + resultSet.getString(2) + " " + resultSet.getString(3) + " " + resultSet.getInt(4);
            } catch (SQLException ignored) {}
        }
        return output;
    }
}
