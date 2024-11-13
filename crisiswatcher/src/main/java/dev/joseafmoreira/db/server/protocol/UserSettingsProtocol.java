package dev.joseafmoreira.db.server.protocol;

import dev.joseafmoreira.db.server.auxiliary.PasswordHandler;
import dev.joseafmoreira.db.server.manager.Manager;

public abstract class UserSettingsProtocol {
    public static String processInput(String input) {
        String lowerInput = input.toLowerCase();
        String output = null;
        if (lowerInput.contains("/username")) 
            output = changeUsername(input);
        else if (lowerInput.contains("/password")) 
            output = changePassword(input);

        return output;
    }

    private static String changeUsername(String input) {
        String output = "Erro ao alterar o nome de utilizador";
        String[] splitedInput = input.split(" ");
        if (splitedInput.length == 3) {
            Boolean changeUsername = (Manager.getInstance()).changeUsername(splitedInput[1], splitedInput[2]);
            if (changeUsername) output = "Nome de utilizador alterado com sucesso";
        }

        return output;
    }

    private static String changePassword(String input) {
        String output = "Erro ao alterar a palavra-passe do utilizador";
        String[] splitedInput = input.split(" ");
        if (splitedInput.length == 3) {
            splitedInput[2] = PasswordHandler.hashPassword(splitedInput[2]);
            Boolean changePassword = (Manager.getInstance()).changePassword(splitedInput[1], splitedInput[2]);
            if (changePassword) output = "Palavra-passe alterada com sucesso";
        }

        return output;
    }
}
