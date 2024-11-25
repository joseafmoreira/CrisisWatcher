package dev.crisiswatcher.server.protocol;

import dev.crisiswatcher.server.password.PasswordHandler;
import dev.crisiswatcher.server.schema.User.UserProfile;
import dev.crisiswatcher.server.manager.DBManager;

public abstract class AuthenticationProtocol {
    public static String processInput(String input) {
        String output = null;
        if (input.startsWith("/login")) 
            output = login(input);
        else if (input.startsWith("/register")) 
            output = register(input);

        return output;
    }

    private static String login(String input) {
        String output = "Erro na autenticação";
        String[] splitedInput = input.split(" ");
        if (splitedInput.length == 3) {
            splitedInput[2] = PasswordHandler.hashPassword(splitedInput[2]);
            String loginResult = (DBManager.getInstance()).getUser(splitedInput[1], splitedInput[2]);
            if (loginResult != null) output = "/login " + loginResult;
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
                boolean insertUser = (DBManager.getInstance()).insertUser(splitedInput[1], splitedInput[2], Integer.valueOf(splitedInput[3]));
                if (insertUser) output = "Utilizador registrado com sucesso";
            }
        }

        return output;
    }
}
