package dev.crisiswatcher.server.protocol;

import dev.crisiswatcher.server.db.Manager;
import dev.crisiswatcher.server.model.UserModel;

public abstract class UserChatProtocol {
    public static String processInput(UserModel userModel, String input) {
        String output = null;
        if (input.startsWith("/msg")) {
            String[] splittedInput = input.split(" ");
            if (splittedInput.length > 2) {
                String content = "";
                for (int i = 2; i < splittedInput.length; i++) 
                content += splittedInput[i] + " ";
                output = sendMessage(userModel.getName(), userModel.getUuid(), splittedInput[1], content);
            }
        } else if (input.contains("/chat")) {
            String[] splittedInput = input.split(" ");
            if (splittedInput.length == 2)
                output = getMessages(userModel.getName(), userModel.getUuid(), splittedInput[1], true);
        } else if (input.equals("/johncena")) {
            output = getMessages(userModel.getName(), userModel.getUuid(), "all", false);
        }
        return output;
    }

    private static String sendMessage(String senderName, int senderID, String receiver, String content) {
        String output = "Erro ao enviar mensagem para o cliente " + receiver;
        boolean result = (Manager.getInstance()).sendPrivateMessage(senderName, senderID, receiver, content.substring(0, content.length() - 1));
        if (result) output = "A mensagem foi enviada com sucesso para o utilizador " + senderName;
        return output;
    }

    private static String getMessages(String senderName, int senderID, String receiver, boolean all) {
        return (Manager.getInstance()).getMessages(senderName, senderID, receiver, all);
    }
}
