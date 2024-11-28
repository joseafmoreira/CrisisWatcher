package dev.crisiswatcher.server.protocol;

import dev.crisiswatcher.server.manager.DBManager;

public abstract class UserChatProtocol {
    public static String processInput(String input) {
        String output = null;
        if (input.contains("/chat")) {
            output = getChat(input);
        } else if (input.contains("/msg")) {
            output = sendMessage(input);
        }

        return output;
    }

    private static String getChat(String input) {
        String output = "Erro ao obter o chat";
        String[] splitedInput = input.split(" ");
        if (splitedInput.length == 3) {
            (DBManager.getInstance()).getPrivateChat(splitedInput[1], splitedInput[2]);
        }

        return output;
    }

    private static String sendMessage(String input) {
        String output = "Erro ao enviar a mensagem";
        String[] splitedInput = input.split(" ");
        if (splitedInput.length == 4) {
            
        }

        return output;
    }
}
