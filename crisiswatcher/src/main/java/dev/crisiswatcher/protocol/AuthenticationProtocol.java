package dev.crisiswatcher.protocol;

import dev.crisiswatcher.db.server.auxiliary.PasswordHandler;
import dev.crisiswatcher.db.server.manager.Manager;
import dev.crisiswatcher.enums.UserProfile;

public abstract class AuthenticationProtocol {
    public static String processInput(String input) {
        String lowerInput = input.toLowerCase();
        String output = null;
        if (lowerInput.contains("/login")) 
            output = login(input);
        else if (lowerInput.contains("/register")) 
            output = register(input);

        return output;
    }

    private static String login(String input) {
        String output = "Erro na autenticação";
        String[] splitedInput = input.split(" ");
        if (splitedInput.length == 3) {
            splitedInput[2] = PasswordHandler.hashPassword(splitedInput[2]);
            boolean validateUser = (Manager.getInstance()).validateUser(splitedInput[1], splitedInput[2]);
            if (validateUser) output = "Utilizador autenticado com sucesso";
        }

        return output;
    }

    private static String register(String input) {
        String output = "Erro no registro";
        String[] splitedInput = input.split(" ");
        if (splitedInput.length == 4) {
            splitedInput[2] = PasswordHandler.hashPassword(splitedInput[2]);
            UserProfile userProfile = UserProfile.getEnum(splitedInput[3]);
            if (userProfile != null) {
                splitedInput[3] = String.valueOf(userProfile.getKey());
                boolean insertUser = (Manager.getInstance()).insertUser(splitedInput[1], splitedInput[2], Integer.valueOf(splitedInput[3]));
                if (insertUser) output = "Utilizador registrado com sucesso";
            }
        }

        return output;
    }
}
