package dev.crisiswatcher.server.protocol;

import dev.crisiswatcher.server.manager.Manager;
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
                output = sendPrivateMessage(userModel.getName(), userModel.getUuid(), splittedInput[1], content);
            }
        } else if (input.contains("/chat")) {
            String[] splittedInput = input.split(" ");
            if (splittedInput.length == 2) {
                output = (userModel.getName().equals(splittedInput[1])) ? getOwnPrivateChat(userModel.getName(), userModel.getUuid()) : getPrivateChat(userModel.getName(), userModel.getUuid(), splittedInput[1]);
            }
        } else if (input.equals("/unseen")) {
            output = getUnseenPrivateMessages(userModel.getName(), userModel.getUuid());
        } else if (input.startsWith("/seen")) {
            setPrivateMessagesSeen(Integer.valueOf(input.split(" ")[1]), userModel.getUuid());
            output = "/seen";
        }
        return output;
    }

    private static String sendPrivateMessage(String senderName, int senderID, String receiver, String content) {
        String output = "Erro ao enviar mensagem para o cliente " + receiver;
        boolean result = (Manager.getInstance()).sendPrivateMessage(senderName, senderID, receiver, content.substring(0, content.length() - 1));
        if (result) output = "A mensagem foi enviada com sucesso para o utilizador " + receiver;
        return output;
    }

    private static String getOwnPrivateChat(String senderName, int senderID) {
        return (Manager.getInstance()).getOwnPrivateChat(senderName, senderID);
    }

    private static String getPrivateChat(String senderName, int senderID, String receiver) {
        return (Manager.getInstance()).getPrivateChat(senderName, senderID, receiver);
    }

    private static String getUnseenPrivateMessages(String senderName, int senderID) {
        return (Manager.getInstance()).getUnseenPrivateMessages(senderName, senderID);
    }

    private static void setPrivateMessagesSeen(int messageID, int userID) {
        new Thread(() -> {
            while (true) {
                boolean result = (Manager.getInstance()).setPrivateMessagesSeen(messageID, userID);
                if (result) break;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }).start();
    }
}
