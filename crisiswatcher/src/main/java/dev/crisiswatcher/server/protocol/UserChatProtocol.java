package dev.crisiswatcher.server.protocol;

import dev.crisiswatcher.server.db.Manager;
import dev.crisiswatcher.server.model.UserModel;

public abstract class UserChatProtocol {
    public static String processInput(UserModel userModel, String input) {
        String output = null;
        if (input.startsWith("/chat")) {
            String[] splittedInput = input.split(" ");
            if (splittedInput.length == 2) 
                output = getChat(userModel.getName(), splittedInput[1]);
        } else if (input.startsWith("/msg")) {
            output = sendMessage("output", "input", "output");
        }
        return output;
    }

    private static String getChat(String sender, String receiver) {
        String output = "Erro ao obter o chat com o cliente " + receiver;
        String chat = (Manager.getInstance()).getChat(sender, receiver);
        if (!chat.equals("O cliente não existe")) output = chat;
        return output;
    }

    private static String sendMessage(String sender, String receiver, String content) {
        String output = "Erro ao enviar mensagem para o cliente " + receiver;
        return output;
    }
}
