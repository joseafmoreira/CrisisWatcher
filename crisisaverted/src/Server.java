import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DatabaseManager {
    private static final String DB_URL_USERS = "jdbc:sqlite:trabalho.db";
    private static final String DB_URL_MESSAGES = "jdbc:sqlite:messages.db";
    private static final String DB_URL_CHATROOMS = "jdbc:sqlite:chatrooms.db";


    public void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             Statement statement = connection.createStatement()) {

            statement.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, rank INTEGER)");

            statement.execute("CREATE TABLE IF NOT EXISTS chatrooms (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, user TEXT)");

            statement.execute("CREATE TABLE IF NOT EXISTS notifications (id INTEGER PRIMARY KEY AUTOINCREMENT, sender TEXT, message TEXT, rank INTEGER, timestamp TEXT)");

            statement.execute("CREATE TABLE IF NOT EXISTS notifications_unread (id INTEGER, user TEXT)");

            statement.execute("CREATE TABLE IF NOT EXISTS requests (id INTEGER PRIMARY KEY AUTOINCREMENT, user TEXT, message TEXT, timestamp TEXT, state TEXT)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public synchronized void createConversationTableIfNotExists(String tableName) {
        try (Connection connection = DriverManager.getConnection(DB_URL_MESSAGES);
             Statement statement = connection.createStatement()) {

            statement.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (id INTEGER PRIMARY KEY AUTOINCREMENT, sender TEXT, timestamp TEXT, message TEXT)");

            notify();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public synchronized boolean userExists(String username){
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?")) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            notify();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized void insertUser(String username, String password, int rank) {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password, rank) VALUES (?, ?, ?)")) {

            statement.setString(1, username);
            if (userExists(username)) {
                notify();
                return;
            }
            statement.setString(2, password);
            statement.setInt(3, rank);
            statement.executeUpdate();

            notify();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean validateUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {

            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            notify();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized boolean groupExists(String name) {
        try (Connection connection = DriverManager.getConnection(DB_URL_CHATROOMS);
             PreparedStatement statement = connection.prepareStatement("SELECT name FROM sqlite_master WHERE type='table' AND name=?")) {

            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            notify();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
        return false;

    }

    public synchronized void insertGroup(String groupName, String user) {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO chatrooms (name, user) VALUES (?, ?)")) {

            statement.setString(1, groupName);
            statement.setString(2, user);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        insertGroupChat(groupName);
        notify();
    }

    public synchronized boolean isGroupNameExists(String groupName) {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM chatrooms WHERE name = ?")) {

            statement.setString(1, groupName);
            ResultSet resultSet = statement.executeQuery();
            notify();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            notify();
            return false;
        }
    }

    public synchronized void addUserToGroup(String groupName, String user) {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement("UPDATE chatrooms SET user = ? WHERE name = ?")) {

            statement.setString(1, user);
            statement.setString(2, groupName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
    }

    public synchronized void insertMessage(String tableName, String sender, String message) {
        try (Connection connection = DriverManager.getConnection(DB_URL_MESSAGES);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO " + tableName + " (sender, timestamp, message) VALUES (?, datetime('now'), ?)")) {

            statement.setString(1, sender);
            statement.setString(2, message);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
    }

    public synchronized void insertMessageGroup(String tableName, String sender, String message) {
        try (Connection connection = DriverManager.getConnection(DB_URL_CHATROOMS);
             PreparedStatement statement = connection.prepareStatement("INSERT INTO " + tableName + " (sender, timestamp, message) VALUES (?, datetime('now'), ?)")) {

            statement.setString(1, sender);
            statement.setString(2, message);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
    }

    public synchronized List<String> getUserChatrooms(String user) {
        List<String> chatrooms = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement("SELECT name FROM chatrooms WHERE user = ?")) {

            statement.setString(1, user);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                chatrooms.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
        return chatrooms;
    }

    public synchronized List<String> getGroupMessages(String tableName) {
        List<String> messages = new ArrayList<>();
        try (
                Connection connection = DriverManager.getConnection(DB_URL_CHATROOMS);
                PreparedStatement statement = connection.prepareStatement("SELECT sender, message FROM " + tableName + " ORDER BY id")) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String sender = resultSet.getString("sender");
                String message = resultSet.getString("message");
                messages.add(sender + ": " + message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
        return messages;
    }

    public synchronized List<String> getUserMessages(String tableName) {
        List<String> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL_MESSAGES);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT sender, message FROM " + tableName + " ORDER BY id");
            while (resultSet.next()) {
                String sender = resultSet.getString("sender");
                String message = resultSet.getString("message");

                messages.add(sender + ": " + message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
        return messages;
    }

    public static void insertGroupChat(String groupName) {
        try (
                Connection connection = DriverManager.getConnection(DB_URL_CHATROOMS);
                Statement statement = connection.createStatement()) {

            statement.execute("CREATE TABLE IF NOT EXISTS " + groupName + " (id INTEGER PRIMARY KEY AUTOINCREMENT, sender TEXT, timestamp TEXT, message TEXT)");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendBroadcast(String sender, String message) {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO notifications (sender, message, rank, timestamp) VALUES (?, ?, 0, datetime('now'))")) {

            statement.setString(1, sender);
            statement.setString(2, message);

            statement.executeUpdate();
            List<String> users = getAllUsers();


            users.remove(sender);
            insertIntoNotificationsUnread(getLastNotification(), users);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
    }

    public synchronized List<String> getBroadcast() {
        List<String> notifications = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT sender, message, rank, timestamp FROM notifications WHERE rank = 0")) {


            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String sender = resultSet.getString("sender");
                String timestamp = resultSet.getString("timestamp");
                String message = resultSet.getString("message");
                notifications.add(sender + " (" + timestamp + "): " + message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
        return notifications;
    }

    public synchronized void sendMulticast(String sender, String message, String rank) {
        int lastInsertedId = -1;
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO notifications (sender, message, rank, timestamp) VALUES (?, ?, ?, datetime('now'))")) {

            statement.setString(1, sender);
            statement.setString(2, message);
            statement.setString(3, rank);

            statement.executeUpdate();

            List<String> users = getUsersWithRank(rank);


            users.remove(sender);
            insertIntoNotificationsUnread(getLastNotification(), users);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
    }

    public int getLastNotification() throws SQLException {
        int lastNotificationID = -1;
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT ID FROM notifications ORDER BY ID DESC LIMIT 1")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    lastNotificationID = resultSet.getInt("ID");
                }
            }
        }
        return lastNotificationID;
    }

    public List<String> getUsersWithRank(String rank) throws SQLException {
        List<String> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement("SELECT username FROM users WHERE rank = ?")) {
            statement.setString(1, rank);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(resultSet.getString("username"));
            }
        }
        return users;
    }

    public void insertIntoNotificationsUnread(int notificationID, List<String> users) throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO notifications_unread (id, user) VALUES (?, ?)")) {
            for (String user : users) {
                statement.setInt(1, notificationID);
                statement.setString(2, user);
                statement.executeUpdate();
            }
        }
    }

    private List<String> getAllUsers() throws SQLException {
        List<String> allUsers = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement("SELECT username FROM users")) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allUsers.add(resultSet.getString("username"));
            }
        }
        return allUsers;
    }

    public synchronized boolean doesUserHaveNotification(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id FROM notifications_unread WHERE user = ?")) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                notify();
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public synchronized int getUserRank(String user) {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT rank FROM users WHERE username = ?")) {
            statement.setString(1, user);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    notify();
                    return resultSet.getInt("rank");
                } else {
                    notify();
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public synchronized List<Integer> getUnreadNotificationsForUser(String username){
        List<Integer> unreadNotifications = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id FROM notifications_unread WHERE user = ?")) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int notificationID = resultSet.getInt("id");
                    unreadNotifications.add(notificationID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
        return unreadNotifications;
    }

    public synchronized void insertRequest(String sender, String message) {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO requests (user, message, timestamp, state) VALUES (?, ?, datetime('now'), 'PENDING')")) {

            statement.setString(1, sender);
            statement.setString(2, message);

            statement.executeUpdate();
            notify();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<String> getPendingRequests() {
        List<String> requests = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT id, user, message, timestamp FROM requests WHERE state = 'PENDING'");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String user = resultSet.getString("user");
                String message = resultSet.getString("message");
                String timestamp = resultSet.getString("timestamp");
                requests.add("ID: " + id + ", " + user + ": " + message + " (Timestamp: " + timestamp + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
        return requests;
    }

    public synchronized List<String> getRequestsForUser(String username) {
        List<String> requests = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT user, message, timestamp, state FROM requests WHERE user = ?")) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String user = resultSet.getString("user");
                String message = resultSet.getString("message");
                String timestamp = resultSet.getString("timestamp");
                String state = resultSet.getString("state");
                requests.add(user + ": " + message + " (Timestamp: " + timestamp + ", State: " + state + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
        return requests;
    }

    public synchronized boolean isRequestPending(int requestId) {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT state FROM requests WHERE id = ?")) {

            preparedStatement.setInt(1, requestId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String state = resultSet.getString("state");
                notify();
                return "PENDING".equals(state);
            } else {
                notify();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            notify();
            return false;
        }
    }

    public synchronized void denyRequest(int requestId) {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE requests SET state = 'DENIED' WHERE id = ?")) {

            statement.setInt(1, requestId);
            statement.executeUpdate();
            notify();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized void approveRequest(int requestId) {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE requests SET state = 'APPROVED' WHERE id = ?")) {

            statement.setInt(1, requestId);
            statement.executeUpdate();
            notify();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public synchronized List<String> getNotifications(List<Integer> notificationIDs) {
        List<String> notifications = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS)) {
            for (int notificationID : notificationIDs) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT sender, message, rank, timestamp FROM notifications WHERE id = ?")) {
                    statement.setInt(1, notificationID);


                    ResultSet resultSet = statement.executeQuery();
                    while (resultSet.next()) {
                        String sender = resultSet.getString("sender");
                        String timestamp = resultSet.getString("timestamp");
                        String message = resultSet.getString("message");
                        notifications.add(sender + " (" + timestamp + "): " + message);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
        return notifications;
    }

    public synchronized List<String> getNotificationsByRank(int rank) {
        List<String> notifications = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS);
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT sender, message, rank, timestamp FROM notifications WHERE rank = ?")) {

            statement.setInt(1, rank);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String sender = resultSet.getString("sender");
                String timestamp = resultSet.getString("timestamp");
                String message = resultSet.getString("message");
                notifications.add(sender + " (" + timestamp + "): " + message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
        return notifications;
    }


    public synchronized void deleteUnreadNotificationsForUser(String username) {
        try (Connection connection = DriverManager.getConnection(DB_URL_USERS)) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM notifications_unread WHERE user = ?")) {
                statement.setString(1, username);
                statement.executeUpdate();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        notify();
    }


}

public class Server {
    private static DatabaseManager databaseManager = new DatabaseManager();

    public static void main(String[] args) {

        databaseManager.initializeDatabase();
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String option;
            String user = "";

            while ((option = in.readLine()) != null) {
                switch (option) {
                    case "1":
                        user = loginUser(in, out);
                        if (!user.isEmpty()) {
                            menu(in, out, user);
                        }
                        break;
                    case "2":
                        registerUser(in, out);
                        break;
                    case "0":
                        clientSocket.close();
                        System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
                        return;
                    default:
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void registerUser(BufferedReader in, PrintWriter out) throws IOException{
        try {
            String username = in.readLine();
            String password = in.readLine();
            int rank = Integer.parseInt(in.readLine());

            if (databaseManager.userExists(username)) {
                out.println("Username taken");
                return;
            }
            databaseManager.insertUser(username, password, rank);

            out.println("Registration successful");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static String loginUser(BufferedReader in, PrintWriter out) throws IOException {
        try {
            String username = in.readLine();
            String password = in.readLine();

            if (databaseManager.validateUser(username, password)) {
                out.println("Login Success");
                return username;
            } else {
                out.println("Invalid username or password. Please try again.");
                return "";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static void menu(BufferedReader in, PrintWriter out, String user) throws IOException {
        out.println("Welcome, " + user);
        while (true) {
            String chatOption = in.readLine();
            switch (chatOption) {
                case "1":
                    showChatMenu(in, out, user);
                    break;
                case "2":
                    menuNotifications(in, out, user);
                    break;
                case "3":
                    if (databaseManager.getUserRank(user) == 1) {
                        out.println("Low rank");
                        menuRequestsLowRank(in, out, user);
                    } else {
                        out.println("");
                        menuRequestsHighRank(in, out);
                    }
                    break;
                case "0":
                    user = "";
                    return;
                default:
                    break;
            }
        }
    }

    private static void menuRequestsLowRank(BufferedReader in, PrintWriter out, String user) throws IOException {
        while (true) {
            String chatOption = in.readLine();

            switch (chatOption) {
                case "1":
                    displayRequestsOfUser(out, user);
                    break;
                case "2":
                    request(in, out, user);
                    break;
                case "0":
                    return;
                default:
                    break;
            }
        }
    }

    private static void menuRequestsHighRank(BufferedReader in, PrintWriter out) throws IOException {
        while (true) {
            String chatOption = in.readLine();
            switch (chatOption) {
                case "1":
                    displayPendingRequests(out);
                    break;
                case "2":
                    checkRequestState(in, out);
                    break;
                case "0":
                    return;
                default:
                    break;
            }
        }
    }


    private static void request(BufferedReader in, PrintWriter out, String sender) throws IOException {
        String message = in.readLine();
        databaseManager.insertRequest(sender, message);
    }

    private static void checkRequestState(BufferedReader in, PrintWriter out) throws IOException {
        int ID = Integer.parseInt(in.readLine());
        if (databaseManager.isRequestPending(ID)) {
            out.println("Please select what an option:");
            approveDenyRequest(in, out, ID);

        } else {
            out.println("");

        }

    }

    private static void approveDenyRequest(BufferedReader in, PrintWriter out, int ID) throws IOException {
        while (true) {
            String chatOption = in.readLine();
            switch (chatOption) {
                case "1":
                    databaseManager.approveRequest(ID);
                    return;
                case "2":
                    databaseManager.denyRequest(ID);
                    return;
                case "0":
                    return;
                default:
                    break;
            }
        }
    }


    public static void displayRequestsOfUser(PrintWriter out, String user){
        List<String> requests = databaseManager.getRequestsForUser(user);
        for (String request : requests) {
            out.println(request);
        }
        out.println("END_OF_REQUESTS");
    }

    public static void displayPendingRequests(PrintWriter out) {
        List<String> requests = databaseManager.getPendingRequests();
        for (String request : requests) {
            out.println(request);
        }
        out.println("END_OF_REQUESTS");
    }

    private static void menuNotifications(BufferedReader in, PrintWriter out, String user) throws IOException {
        if (databaseManager.doesUserHaveNotification(user)) {
            out.println(databaseManager.getUnreadNotificationsForUser(user).size());
        } else {
            out.println();
        }
        while (true) {
            String chatOption = in.readLine();
            switch (chatOption) {
                case "1":
                    broadcast(in, out, user);
                    break;
                case "2":
                    menuMulticast(in, out, user);
                    break;
                case "3":
                    displayNotificationsUnread(out, user);
                    databaseManager.deleteUnreadNotificationsForUser(user);
                    break;
                case "4":
                    displayNotifications(out, user);
                    break;
                case "5":
                    displayBroadcasts(out);
                    break;
                case "0":
                    return;
                default:
                    break;
            }
        }
    }

    public static void displayNotificationsUnread(PrintWriter out, String user) {
        List<String> notifications = databaseManager.getNotifications(databaseManager.getUnreadNotificationsForUser(user));
        for (String notification : notifications) {
            out.println(notification);
        }
        out.println("END_OF_NOTIFICATIONS");
    }

    public static void displayNotifications(PrintWriter out, String user) {
        List<String> notifications = databaseManager.getNotificationsByRank(databaseManager.getUserRank(user));
        for (String notification : notifications) {
            out.println(notification);
        }
        out.println("END_OF_NOTIFICATIONS");
    }

    public static void displayBroadcasts(PrintWriter out) {
        List<String> notifications = databaseManager.getBroadcast();
        for (String notification : notifications) {
            out.println(notification);
        }
        out.println("END_OF_NOTIFICATIONS");
    }

    private static void broadcast(BufferedReader in, PrintWriter out, String sender) throws IOException {
        String message = in.readLine();

        databaseManager.sendBroadcast(sender, message);

    }


    private static void menuMulticast(BufferedReader in, PrintWriter out, String user) throws IOException {
        String chatOption = in.readLine();
        switch (chatOption) {
            case "1":
                multicast(in, out, user, String.valueOf(1));
                break;
            case "2":
                multicast(in, out, user, String.valueOf(2));
                break;
            case "3":
                multicast(in, out, user, String.valueOf(3));
                break;
            case "0":
                return;
            default:
                break;
        }
    }


    private static void multicast(BufferedReader in, PrintWriter out, String sender, String rank) throws IOException {
        String message = in.readLine();

        databaseManager.sendMulticast(sender, message, rank);
    }

    private static void showChatMenu(BufferedReader in, PrintWriter out, String user) throws IOException{
        while (true) {
            String chatOption = in.readLine();
            switch (chatOption) {
                case "1":
                    chatUser(in, out, user);
                    break;
                case "2":
                    handleViewGroupsMenu(in, out, user);
                    break;
                case "0":
                    return;
                default:
                    break;
            }
        }
    }

    private static void chatUser(BufferedReader in, PrintWriter out, String sender) throws IOException {
        String receiver = in.readLine();

        if (databaseManager.userExists(receiver)) {
            String tableName = generateTableName(sender, receiver);

            databaseManager.createConversationTableIfNotExists(tableName);

            handleConversation(in, out, sender, tableName);
        } else {
            out.println("User does not exist.");
        }
    }


    private static void chatGroup(BufferedReader in, PrintWriter out, String sender) throws IOException {
        listUserChatrooms(out, sender);
        String receiver = in.readLine();

        if (databaseManager.groupExists(receiver)) {
            handleConversationGroup(in, out, sender, receiver);
        } else {
            out.println("Group does not exist.");
        }
    }

    private static String generateTableName(String user1, String user2) {
        String[] sortedUsers = {user1, user2};
        Arrays.sort(sortedUsers);
        return sortedUsers[0] + "_" + sortedUsers[1];
    }


    private static void handleConversation(BufferedReader in, PrintWriter out, String sender, String tableName) throws IOException {
        out.println("You are now in a chat with " + tableName);

        showPreviousMessages(out, tableName);

        while (true) {
            String message = in.readLine();
            if (message.equalsIgnoreCase("")) {
                break;
            }

            databaseManager.insertMessage(tableName, sender, message);
        }
    }

    private static void handleConversationGroup(BufferedReader in, PrintWriter out, String sender, String name) throws IOException {
        out.println("You are now in a chat with " + name);

        showPreviousMessagesGroup(out, name);

        while (true) {
            String message = in.readLine();
            if (message.equalsIgnoreCase("")) {
                break;
            }

            databaseManager.insertMessageGroup(name, sender, message);
        }
    }

    private static void showPreviousMessages(PrintWriter out, String tableName) {
        List<String> messages = databaseManager.getUserMessages(tableName);
        for (String message : messages) {
            out.println(message);
        }
        out.println("END_OF_MESSAGES");
    }


    private static void showPreviousMessagesGroup(PrintWriter out, String tableName) {
        List<String> messages = databaseManager.getGroupMessages(tableName);

        for (String message : messages) {
            out.println(message);
        }

        out.println("END_OF_MESSAGES");
    }

    private static void handleViewGroupsMenu(BufferedReader in, PrintWriter out, String user) throws IOException {
        while (true) {
            String choice = in.readLine();

            switch (choice) {
                case "1":
                    joinGroup(in, out, user);
                    break;
                case "2":
                    createGroup(in, out, user);
                    break;
                case "3":
                    chatGroup(in, out, user);
                    break;
                case "0":
                    return;
                default:
                    break;
            }
        }
    }

    private static void createGroup(BufferedReader in, PrintWriter out, String user) throws IOException {
        String groupName = in.readLine().trim();

        if (groupName.isEmpty()) {
            out.println("Group name cannot be empty. Please try again.");
            return;
        }

        if (databaseManager.isGroupNameExists(groupName)) {
            out.println("Group name already exists. Please choose a different name.");
        } else {
            databaseManager.insertGroup(groupName, user);

            databaseManager.addUserToGroup(groupName, user);

            out.println("Group created successfully!");
        }
    }

    private static void joinGroup(BufferedReader in, PrintWriter out, String user) throws IOException {
        out.println("Enter the group name you want to join:");
        String groupName = in.readLine().trim();

        if (databaseManager.isGroupNameExists(groupName)) {

            databaseManager.addUserToGroup(groupName, user);
            out.println("You have joined the group: " + groupName);
        } else {
            out.println("Group does not exist.");
        }
    }

    private static void listUserChatrooms(PrintWriter out, String user) {
        List<String> chatrooms = databaseManager.getUserChatrooms(user);

        for (String chatroom : chatrooms) {
            out.println(chatroom);
        }

        out.println("END_OF_CHATROOMS");
    }

}
