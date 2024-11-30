package dev.crisiswatcher.server.manager;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import dev.crisiswatcher.server.file.FileHandler;
import dev.crisiswatcher.server.logger.Logger;

/**
 * Singleton utility class that handles the database. <p>
 * 
 * The available constructors for this {@code Manager} include: <p>
 * <ul>
 *  <li>{@link #Manager()}: Constructs a new Manager object</li>
 * </ul>
 * 
 * The operations available for this {@code Manager} include:
 * <ul>
 *  <li>{@link #getInstance()}: Returns this instance of the database manager</li>
 *  <li>{@link #initializeDatabase()}: Initializes the database</li>
 *  <li>{@link #checkTable(String)}: Checks if a table exists in the database or not</li>
 *  <li>{@link #createTable(Statement, String, String)}: Creates a table in the database</li>
 * </ul> 
 * 
 * <h3>Manager</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 */
public class Manager {
    /**
     * SQLite main parameters
     */
    private static final String SQLITE = "jdbc:sqlite:";
    /**
     * Databse main folder
     */
    private static final String MAIN_FOLDER = "db/";
    /**
     * CrisisWatcher database file location
     */
    private static final String CRISISWATCHER  = SQLITE + MAIN_FOLDER + "crisiswatcher.db";
    /**
     * The main instance of the manager
     */
    private static Manager instance;
    /**
     * The SQLite connection to the CrisisWatcher database
     */
    private static Connection connection;

    /**
     * Constructs a new Manager object.
     */
    private Manager() {
        try {
            FileHandler.createFolder(MAIN_FOLDER);
            connection = DriverManager.getConnection(CRISISWATCHER);
            initializeDatabase();
            Logger.addServerLogEntry("Base de dados iniciada com sucesso");
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao iniciar a base de dados: " + e.getMessage());
        }
    }

    /**
     * Returns this instance of the database manager.
     * 
     * @return this instance of the database manager
     */
    public static Manager getInstance() {
        if (instance == null) instance = new Manager();

        return instance;
    }

    /**
     * Registers a user in the database
     * 
     * @param username the specified username
     * @param password the specified password
     * @param profile the specified profile
     * @return true if the operation was successful, false otherwise
     */
    public synchronized boolean registerUser(String username, String password, int profile) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (username, password, profile) VALUES (?, ?, ?)");
            ResultSet resultSet = getUser(username);
            if (resultSet != null && !resultSet.next()) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setInt(3, profile);
                preparedStatement.executeUpdate();
                Logger.addServerLogEntry("O utilizador " + username + " foi inserido com sucesso");
                return true;
            }
            Logger.addServerLogEntry("O utilizador " + username + " já existe");
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao inserir um utilizador: " + e.getMessage());
        }

        return false;
    }

    /**
     * Authenticates a user.
     * 
     * @param username the specified username
     * @param password the specified password
     * @return true if the operation was successful, false otherwise
     */
    public synchronized String loginUser(String username, String password) {
        String output = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Logger.addServerLogEntry("O utilizador " + username + " foi autenticado com sucesso");
                output = resultSet.getInt(1) + " " + resultSet.getString(2) + " " + resultSet.getInt(4);
            }
            Logger.addServerLogEntry("O utilizador " + username + " não foi autenticado com sucesso");
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao autenticar o utilizador " + username + ": " + e.getMessage());
        }

        return output;
    }

    /**
     * Changes a user's name.
     * 
     * @param oldUsername the specified oldUsername
     * @param newUsername the specified newUsername
     * @return true if the operation was successful, false otherwise
     */
    public synchronized boolean updateUsername(String oldUsername, String newUsername) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET username = ? WHERE username = ?");
            ResultSet oldUserResultSet = getUser(oldUsername);
            ResultSet newUserResultSet = getUser(newUsername);
            if (oldUserResultSet != null && oldUserResultSet.next() && newUserResultSet != null && !newUserResultSet.next()) {
                statement.setString(1, newUsername);
                statement.setString(2, oldUsername);
                statement.executeUpdate();
                Logger.addServerLogEntry("Nome de utilizador alterado de " + oldUsername + " para " + newUsername + " com sucesso");
                return true;
            }
            Logger.addServerLogEntry("Não foi possível alterar o nome de utilizador de " + oldUsername + " para " + newUsername);
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao alterar o nome de utilizador: " + e.getMessage());
        }
        return false;
    }

    /**
     * Changes a user's password.
     * 
     * @param username the specified username
     * @param newPassword the specified new password
     * @return true if the operation was successful, false otherwise
     */
    public synchronized boolean updatePassword(String username, String newPassword) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET password = ? WHERE username = ?");
            ResultSet userResultSet = getUser(username);
            if (userResultSet != null && userResultSet.next()) {
                statement.setString(1, newPassword);
                statement.setString(2, username);
                statement.executeUpdate();
                Logger.addServerLogEntry("Palavra-passe do utilizador " + username + " alterada com sucesso");

                return true;
            }
            Logger.addServerLogEntry("A palavra-passe do utilizador " + username + " não foi alterada"); 
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao alterar a palavra-passe do utilizador " + username + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Changes a user's profile.
     * 
     * @param username the specified username
     * @param newProfile the specified new profile
     * @return true if the operation was successful, false otherwise
     */
    public synchronized boolean updateProfile(String username, int newProfile) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET profile = ? WHERE username = ?");
            ResultSet userResultSet = getUser(username);
            if (userResultSet != null && userResultSet.next()) {
                statement.setInt(1, newProfile);
                statement.setString(2, username);
                statement.executeUpdate();
                Logger.addServerLogEntry("Perfil do utilizador " + username + " alterado com sucesso");

                return true;
            }
            Logger.addServerLogEntry("O perfil do utilizador " + username + " não foi alterado");
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao alterar o perfil do utilizador " + username + ": " + e.getMessage());
        }
        return false;
    }

    public synchronized boolean sendPrivateMessage(String senderName, int senderID, String receiver, String content) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO private_messages (sender, receiver, content, seen) VALUES (?, ?, ?, ?)");
            ResultSet receiverResultSet = getUser(receiver);
            if (receiverResultSet != null && receiverResultSet.next()) {
                statement.setInt(1, (senderID == receiverResultSet.getInt(1)) ? 0 : senderID);
                statement.setInt(2, receiverResultSet.getInt(1));
                statement.setString(3, content);
                statement.setInt(4, (senderID == receiverResultSet.getInt(1)) ? 1 : 0);
                statement.executeUpdate();
                Logger.addServerLogEntry("A mensagem de " + senderName + " para " + receiver + " foi registada com sucesso");
                return true;
            }
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao enviar uma mensagem para o utilizador " + receiver + ": " + e.getMessage());
        }
        return false;
    }

    public synchronized String getUnseenPrivateMessages(String senderName, int senderID) {
        String result = "Erro ao obter as mensagens não lidas do utilizador " + senderName;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM private_messages WHERE receiver = ? AND seen = 0");
            statement.setInt(1, senderID);
            ResultSet resultSet = statement.executeQuery();
            result = "/msgs ";
            while (resultSet.next()) 
                result += resultSet.getInt(1) + "/" + getUser((resultSet.getInt(2) == 0) ? resultSet.getInt(3) : resultSet.getInt(2)).getString(2) + "/" + getUser(resultSet.getInt(3)).getString(2) + "/" + resultSet.getString(4).replaceAll(" ", "_") + "/" + resultSet.getInt(5) + " ";
            result = result.substring(0, result.length() - 1);
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao obter as mensagens não lidas do utilizador " + senderName + ": " + e.getMessage());
        }
        return result;
    }

    public synchronized String getOwnPrivateChat(String senderName, int senderID) {
        String result = "Erro ao obter as mensagens privadas do utilizador " + senderName;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM private_messages WHERE sender = 0 AND receiver = ?");
            statement.setInt(1, senderID);
            ResultSet resultSet = statement.executeQuery();
            result = "/msgs ";
            while (resultSet.next()) 
                result += resultSet.getInt(1) + "/" + getUser(resultSet.getInt(3)).getString(2) + "/" + getUser(resultSet.getInt(3)).getString(2) + "/" + resultSet.getString(4).replaceAll(" ", "_") + "/" + resultSet.getInt(5) + " ";
            Logger.addServerLogEntry("O chat privado do utilizador " + senderName + " foi obtido com sucesso");
            result = result.substring(0, result.length() - 1);
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao obter o chat privado do utilizador " + senderName + ": " + e.getMessage());
        }
        return result;
    }

    public synchronized String getPrivateChat(String senderName, int senderID, String receiver) {
        String result = "Erro ao obter as mensagens do utilizador " + senderName + " com o utilizador " + receiver;
        try {
            ResultSet userResultSet = getUser(receiver);
            if (userResultSet == null || !userResultSet.next()) throw new SQLException("User " + receiver + " doesn't exist");
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM private_messages WHERE (sender = ? AND receiver = ?) OR (sender = ? AND receiver = ?)");
            statement.setInt(1, senderID);
            statement.setInt(2, userResultSet.getInt(1));
            statement.setInt(3, userResultSet.getInt(1));
            statement.setInt(4, senderID);
            ResultSet resultSet = statement.executeQuery();
            result = "/msgs ";
            while (resultSet.next()) 
                result += resultSet.getInt(1) + "/" + getUser(resultSet.getInt(2)).getString(2) + "/" + getUser(resultSet.getInt(3)).getString(2) + "/" + resultSet.getString(4).replaceAll(" ", "_") + "/" + resultSet.getInt(5) + " ";
            Logger.addServerLogEntry("O chat do utilizador " + senderName + " com o utilizador " + receiver + " foi obtido com sucesso");
            result = result.substring(0, result.length() - 1);
        } catch (SQLException e) {
            if (e.getMessage().equals("User " + receiver + " doesn't exist")) result = "O utilizador " + receiver + " não foi encontrado";
            Logger.addServerLogEntry("Erro ao obter o chat do utilizador " + senderName + " com o utilizador " + receiver + ": " + e.getMessage());
        }
        return result;
    }

    public synchronized boolean setPrivateMessagesSeen(int messageID, int receiverID) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE private_messages SET seen = 1 WHERE uuid = ? AND receiver = ?");
            statement.setInt(1, messageID);
            statement.setInt(2, receiverID);
            statement.executeUpdate();
            Logger.addServerLogEntry("A mensagem " + messageID + " foi vista");
            return true;
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao visualizar a mensagem " + messageID);
        }
        return false;
    }

    /**
     * Initializes the database.
     * 
     * @throws SQLException if there's an error in the SQL queries
     */
    private void initializeDatabase() throws SQLException {
        Statement statement = connection.createStatement();
        if (!checkTable("users")) 
            createTable(statement, "users", "uuid INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, profile INTEGER");
        if (!checkTable("private_messages"))
            createTable(statement, "private_messages", "uuid INTEGER PRIMARY KEY AUTOINCREMENT, sender INTEGER, receiver INTEGER, content TEXT, seen int, FOREIGN KEY(sender) REFERENCES users(uuid), FOREIGN KEY(receiver) REFERENCES users(uuid)");
    }

    /**
     * Checks if a table exists in the database or not.
     * 
     * @param name the name of the table
     * @return true if the table exists in the database, false otherwise
     * @throws SQLException if there's an error in the SQL queries
     */
    private boolean checkTable(String name) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, name, new String[]{"TABLE"});

        return resultSet.next();
    }

    /**
     * Creates a table in the database.
     * 
     * @param statement the statement to execute
     * @param name the name of the table
     * @param parameters the parameters of the table
     */
    private void createTable(Statement statement, String name, String parameters) {
        try {
            statement.execute("CREATE TABLE " + name + " (" + parameters + ")");
            Logger.addServerLogEntry("A tabela " + name + " foi criada");
        } catch (SQLException ignored) {
            Logger.addServerLogEntry("Erro ao criar a tabela " + name);
        }
    }

    private synchronized ResultSet getUser(String username) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            Logger.addServerLogEntry("O utilizador " + username + " foi obtido da base de dados");
            return resultSet;
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao obter um utilizador: " + e.getMessage());
        }
        return null;
    }

    private synchronized ResultSet getUser(int uuid) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE uuid = ?");
            preparedStatement.setInt(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            Logger.addServerLogEntry("O utilizador " + resultSet.getString(2) + " foi obtido da base de dados");
            return resultSet;
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao obter um utilizador: " + e.getMessage());
        }
        return null;
    }
}
