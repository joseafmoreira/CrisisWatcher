package dev.joseafmoreira.db.server.manager;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dev.joseafmoreira.file.FileManager;
import dev.joseafmoreira.log.LogHandler;

public class Manager {
    private static final String SQLITE = "jdbc:sqlite:";
    private static final String DB_FOLDER = "db";
    private static final String CRISISWATCHER  = SQLITE + DB_FOLDER + "/crisiswatcher.db";
    private static Manager instance;
    private static boolean UPDATE_DB;
    private static Connection connection;

    private Manager() {
        try {
            FileManager.createFolder(DB_FOLDER);
            connection = DriverManager.getConnection(CRISISWATCHER);
            LogHandler.addDBLogEntry("Conexão com a base de dados estabelecida com sucesso");
            initializeDatabase();
            UPDATE_DB = false;
        } catch (SQLException ignored) {
            System.out.println(ignored.getMessage());
            LogHandler.addDBLogEntry("Erro ao estabelecer conexão com a base de dados");
        }
    }

    public synchronized static Manager getInstance() {
        if (instance == null) instance = new Manager();

        return instance;
    }

    public synchronized boolean insertUser(String username, String password, int profile) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO users (username, password, profile) VALUES (?, ?, ?)");
            Boolean verifyUser = verifyUser(username);
            if (verifyUser != null && !verifyUser) {
                UPDATE_DB = true;
                statement.setString(1, username);
                statement.setString(2, password);
                statement.setInt(3, profile);
                statement.executeUpdate();
                UPDATE_DB = false;
                notify();
                LogHandler.addDBLogEntry("Utilizador inserido com sucesso");

                return true;
            }

            return false;
        } catch (SQLException ignored) {
            LogHandler.addDBLogEntry("Erro ao inserir um utilizador");
            return false;
        }
    }

    public synchronized Boolean verifyUser(String username) {
        try {
            if (UPDATE_DB) wait();
            PreparedStatement statement = connection.prepareStatement("SELECT * from users WHERE username = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            notify();
            LogHandler.addDBLogEntry("Um utilizador foi verificado");

            return resultSet.next();
        } catch (InterruptedException | SQLException ignored) {
            System.out.println("Teste");
            LogHandler.addDBLogEntry("Erro ao verificar um utilizador");
            return null;
        }
    }

    public synchronized Boolean validateUser(String username, String password) {
        try {
            if (UPDATE_DB) wait();
            PreparedStatement statement = connection.prepareStatement("SELECT * from users WHERE username = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            notify();
            boolean result = resultSet.next();
            if (result) LogHandler.addDBLogEntry((result) ? "Um utilizador foi validado" : "Erro ao validar um utilizador");

            return result;
        } catch (InterruptedException | SQLException ignored) {
            LogHandler.addDBLogEntry("Erro ao validar um utilizador");
            return null;
        }
    }

    public synchronized boolean changeUsername(String oldUsername, String newUsername) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET username = ? WHERE username = ?");
            Boolean verifyOldUser = verifyUser(oldUsername);
            Boolean verifyNewUser = verifyUser(newUsername);
            if (verifyOldUser != null && verifyOldUser && verifyNewUser != null && !verifyNewUser) {
                if (UPDATE_DB) wait();
                UPDATE_DB = true;
                statement.setString(1, newUsername);
                statement.setString(2, oldUsername);
                statement.executeUpdate();
                UPDATE_DB = false;
                notify();
                LogHandler.addDBLogEntry("Nome de utilizador alterado com sucesso");

                return true;
            }

            return false;
        } catch (InterruptedException | SQLException ignored) {
            LogHandler.addDBLogEntry("Erro ao alterar o nome de utilizador");
            return false;
        }
    }

    public synchronized boolean changePassword(String username, String newPassword) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET password = ? WHERE username = ?");
            Boolean verifyUser = verifyUser(username);
            if (verifyUser != null && verifyUser) {
                if (UPDATE_DB) wait();
                UPDATE_DB = true;
                statement.setString(1, newPassword);
                statement.setString(2, username);
                statement.executeUpdate();
                UPDATE_DB = false;
                notify();
                LogHandler.addDBLogEntry("Palavra-passe alterada com sucesso");

                return true;
            }

            return false;
        } catch (InterruptedException | SQLException ignored) {
            LogHandler.addDBLogEntry("Erro ao alterar a palavra-passe");
            return false;
        }
    }

    private void initializeDatabase() {
        FileManager.createFolder(DB_FOLDER);
        try {
            Statement statement = connection.createStatement();

            if (!checkTable(connection, "users")) createUsersTable(statement);
        } catch (SQLException ignored) {}
    }

    private boolean checkTable(Connection connection, String name) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, name, new String[]{"TABLE"});

        return resultSet.next();
    }

    private void createUsersTable(Statement statement) {
        createTable(statement, "users", "uuid INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, profile INTEGER");
    }

    private void createTable(Statement statement, String name, String parameters) {
        try {
            statement.execute("CREATE TABLE " + name + " (" + parameters + ")");
            LogHandler.addDBLogEntry("A tabela " + name + " foi criada");
        } catch (SQLException ignored) {
            LogHandler.addDBLogEntry("Erro ao criar a tabela " + name);
        }
    }
}
