import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to server");

            while (true) {
                System.out.println("Pick an option:");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("0. Exit");

                String option = consoleIn.readLine();
                out.println(option);

                switch (option) {
                    case "1":
                        loginUser(consoleIn, out, in);
                        break;
                    case "2":
                        registerUser(consoleIn, out, in);
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void registerUser(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        System.out.println("Enter a username:");
        String username = consoleIn.readLine();
        out.println(username);

        System.out.println("Enter a password:");
        String password = consoleIn.readLine();
        out.println(password);

        System.out.println("Enter a rank (integer):");
        String rank = consoleIn.readLine();
        out.println(rank);

        String serverResponse = server.readLine();
        System.out.println("Server response: " + serverResponse);
    }

    private static void loginUser(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        System.out.println("Enter your username:");
        String username = consoleIn.readLine();
        out.println(username);

        System.out.println("Enter your password:");
        String password = consoleIn.readLine();
        out.println(password);

        String serverResponse = server.readLine();
        System.out.println(serverResponse);

        if (serverResponse.equals("Login Success")) {
            menu(consoleIn, out, server);
        }
    }

    private static void menu(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        String serverResponse = server.readLine();
        System.out.println(serverResponse);
        while (true) {

            System.out.println("Menu:");
            System.out.println("1. Message");
            System.out.println("2. Notifications");
            System.out.println("3. Requests");
            System.out.println("0. Logout");

            String chatOption = consoleIn.readLine();
            out.println(chatOption);

            switch (chatOption) {
                case "1":
                    enterChatMenu(consoleIn, out, server);
                    break;
                case "2":
                    menuNotifications(consoleIn, out, server);
                    break;
                case "3":
                    serverResponse = server.readLine();
                    if (serverResponse.equals("Low rank")) {
                        menuRequestsLowRank(consoleIn, out, server);
                    } else {
                        menuRequestsHighRank(consoleIn, out, server);
                    }
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void menuRequestsLowRank(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {

        while (true) {

            System.out.println("Requests:");
            System.out.println("1. View my requests");
            System.out.println("2. Create request");
            System.out.println("0. Exit");

            String chatOption = consoleIn.readLine();
            out.println(chatOption);
            switch (chatOption) {
                case "1":
                    receiveRequests(server);
                    break;
                case "2":
                    sendRequest(consoleIn, out, server);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void menuRequestsHighRank(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {

        while (true) {

            System.out.println("Requests:");
            System.out.println("1. View pending requests");
            System.out.println("2. Approve/Deny requests");
            System.out.println("0. Exit");

            String chatOption = consoleIn.readLine();
            out.println(chatOption);

            switch (chatOption) {
                case "1":
                    receiveRequests(server);
                    break;
                case "2":
                    modifyRequest(consoleIn, out, server);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }

        }
    }

    private static void receiveRequests(BufferedReader server) throws IOException {
        while (true) {
            String request = server.readLine();
            if (request.equals("END_OF_REQUESTS")) {
                break;
            }
            System.out.println(request);
        }
    }

    private static void enterChatMenu(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        while (true) {

            System.out.println("Menu:");
            System.out.println("1. Chat with user");
            System.out.println("2. View Groups");
            System.out.println("0. Exit");

            String chatOption = consoleIn.readLine();
            out.println(chatOption);

            switch (chatOption) {
                case "1":
                    chatWithUser(consoleIn, out, server);
                    break;
                case "2":
                    viewGroups(consoleIn, out, server);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void modifyRequest(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        System.out.println("What Request would you to modify:");
        int request = Integer.parseInt(consoleIn.readLine());
        out.println(request);
        String serverResponse = server.readLine();
        if (serverResponse.equals("Please select what an option:")) {
            approveDenyRequest(consoleIn, out, server);

        } else {
            System.out.println("Invalid ID");
        }

    }

    private static void approveDenyRequest(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        while (true) {
            System.out.println("Requests:");
            System.out.println("1. Approve");
            System.out.println("2. Deny");
            System.out.println("0. Exit");

            String chatOption = consoleIn.readLine();
            out.println(chatOption);

            switch (chatOption) {
                case "1":
                    return;
                case "2":
                    return;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

    }

    private static void menuNotifications(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        String serverResponse = server.readLine();
        if (!serverResponse.isEmpty()) {
            System.out.println("You have " + serverResponse + " notifications to read!");
        }

        while (true) {

            System.out.println("Menu:");
            System.out.println("1. Send a broadcast");
            System.out.println("2. Send a multicast");
            System.out.println("3. Check unread notifications");
            System.out.println("4. Check notifications");
            System.out.println("5. Check broadcasts");
            System.out.println("0. Exit");

            String chatOption = consoleIn.readLine();
            out.println(chatOption);

            switch (chatOption) {
                case "1":
                    sendBroadcast(consoleIn, out, server);
                    break;
                case "2":
                    menuMulticast(consoleIn, out, server);
                    break;
                case "3":
                    receiveNotifications(server);
                    break;
                case "4":
                    receiveNotifications(server);
                    break;
                case "5":
                    receiveNotifications(server);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void receiveNotifications(BufferedReader server) throws IOException {
        while (true) {
            String notification = server.readLine();
            if (notification.equals("END_OF_NOTIFICATIONS")) {
                break;
            }
            System.out.println(notification);
        }
    }

    private static void menuMulticast(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        while (true) {

            System.out.println("Menu:");
            System.out.println("1. Send to rank 1");
            System.out.println("2. Send to rank 2");
            System.out.println("3. Send to rank 3");
            System.out.println("0. Exit");

            int chatOption = -1;
            while (chatOption < 0 || chatOption > 3) {
                chatOption = Integer.parseInt(consoleIn.readLine());
                if (chatOption < 0 || chatOption > 3) {
                    System.out.println("Invalid option. Please try again.");
                }
            }
            out.println(chatOption);

            if (chatOption == 0) {
                break;
            }

            sendNotification(consoleIn, out, server);
        }
    }

    private static void sendBroadcast(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        System.out.println("Enter your broadcast message:");
        String broadcastMessage = consoleIn.readLine();
        out.println(broadcastMessage);
    }

    private static void sendRequest(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        System.out.println("Enter your Request message:");
        String request = consoleIn.readLine();
        out.println(request);
    }

    private static void sendNotification(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        System.out.println("Enter your multicast message:");
        String broadcastMessage = consoleIn.readLine();
        out.println(broadcastMessage);
    }


    private static void chatWithUser(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        System.out.println("Enter the username you want to chat with:");
        String otherUser = consoleIn.readLine();
        out.println(otherUser);

        String serverResponse = server.readLine();
        System.out.println("Server response: " + serverResponse);

        if (serverResponse.equals("User does not exist.")) {

            return;
        }

        System.out.println("Previous messages:");
        while (true) {
            String previousMessage = server.readLine();
            if (previousMessage.equals("END_OF_MESSAGES")) {
                break;
            }
            System.out.println(previousMessage);
        }

        System.out.println("Start chatting (type nothing to leave the chat):");
        String message;
        while (true) {
            message = consoleIn.readLine();
            out.println(message);

            if (message.equalsIgnoreCase("")) {
                break;
            }
        }
    }

    private static void viewGroups(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        while (true) {
            System.out.println("View Groups Menu:");
            System.out.println("1. Join group");
            System.out.println("2. Create group");
            System.out.println("3. Chat with group");
            System.out.println("0. Exit");

            String choice = consoleIn.readLine();
            out.println(choice);

            switch (choice) {
                case "1":
                    joinGroup(consoleIn, out, server);
                    break;
                case "2":
                    createGroup(consoleIn, out, server);
                    break;
                case "3":
                    chatWithGroup(consoleIn, out, server);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }

        }
    }

    private static void createGroup(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        System.out.println("Enter the group name:");
        String groupName = consoleIn.readLine().trim();

        out.println(groupName);
        String serverResponse = server.readLine();
        System.out.println("Server response: " + serverResponse);
    }

    private static void joinGroup(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        System.out.println("Enter the group name you want to join:");
        String groupName = consoleIn.readLine().trim();

        out.println(groupName);
        String serverResponse = server.readLine();
        System.out.println("Server response: " + serverResponse);
    }


    private static void chatWithGroup(BufferedReader consoleIn, PrintWriter out, BufferedReader server) throws IOException {
        System.out.println("Your Chatrooms:");
        String chatroom;
        while (!(chatroom = server.readLine()).equals("END_OF_CHATROOMS")) {
            System.out.println(chatroom);
        }

        System.out.println("Enter the group you want to chat with:");
        String otherUser = consoleIn.readLine();
        out.println(otherUser);

        String serverResponse = server.readLine();
        System.out.println("Server response: " + serverResponse);

        if (serverResponse.equals("Group does not exist.")) {
            return;
        }

        System.out.println("Previous messages:");
        while (true) {
            String previousMessage = server.readLine();
            if (previousMessage.equals("END_OF_MESSAGES")) {
                break;
            }
            System.out.println(previousMessage);
        }

        System.out.println("Start chatting (type nothing to leave the chat):");
        String message;
        while (true) {
            message = consoleIn.readLine();
            out.println(message);

            if (message.equalsIgnoreCase("")) {
                break;
            }
        }
    }

}
