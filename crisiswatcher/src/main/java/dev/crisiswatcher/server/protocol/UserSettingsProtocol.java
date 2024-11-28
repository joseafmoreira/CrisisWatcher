package dev.crisiswatcher.server.protocol;

import dev.crisiswatcher.server.db.Manager;
import dev.crisiswatcher.server.model.UserModel.PasswordHandler;

public abstract class UserSettingsProtocol {
    public static String processInput(String input) {
        String output = null;
        if (input.contains("/username")) {
            output = changeUsername(input);
        } else if (input.contains("/password")) {
            output = changePassword(input);
        }

        return output;
    }

    private static String changeUsername(String input) {
        String output = "Erro ao mudar o nome de utilizador";
        String[] splitedInput = input.split(" ");
        if (splitedInput.length == 3) {
            boolean changeName = (Manager.getInstance()).updateUsername(splitedInput[2], splitedInput[1]);
            if (changeName) output = "/username " + splitedInput[1];
        }

        return output;
    }

    private static String changePassword(String input) {
        String output = "Erro ao mudar a palavra-passe";
        String[] splitedInput = input.split(" ");
        if (splitedInput.length == 3) {
            splitedInput[1] = PasswordHandler.cipher(splitedInput[1]);
            boolean changePassword = (Manager.getInstance()).updatePassword(splitedInput[2], splitedInput[1]);
            if (changePassword) output = "Palavra-passe alterada com sucesso";
        }

        return output;
    }
}
