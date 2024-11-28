package dev.crisiswatcher.server.db;

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

            return false;
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao alterar o nome de utilizador: " + e.getMessage());
            return false;
        }
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

            return false;
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao alterar a palavra-passe do utilizador " + username + ": " + e.getMessage());
            return false;
        }
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

            return false;
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao alterar o perfil do utilizador " + username + ": " + e.getMessage());
            return false;
        }
    }

    public synchronized String getChat(String sender, String receiver) {
        String result = "O cliente não existe";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM private_messages WHERE sender = ? OR receiver = ? AND sender = ? OR receiver = ?");
        }
        return result;
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
            createTable(statement, "private_messages", "uuid INTEGER PRIMARY KEY AUTOINCREMENT, sender INTEGER, receiver INTEGER, Content TEXT, DateTime DATETIME, int seen, FOREIGN KEY(sender) REFERENCES users(uuid), FOREIGN KEY(receiver) REFERENCES users(uuid)");
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
}
