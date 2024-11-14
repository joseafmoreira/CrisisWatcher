package dev.crisiswatcher.server.manager;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import dev.crisiswatcher.file.FileHandler;
import dev.crisiswatcher.logger.Logger;

public class DBManager {
    private static final String SQLITE = "jdbc:sqlite:";
    private static final String MAIN_FOLDER = "db/";
    private static final String CRISISWATCHER  = SQLITE + MAIN_FOLDER + "crisiswatcher.db";
    private static DBManager instance;
    private static Connection connection;

    private DBManager() {
        try {
            FileHandler.createFolder(MAIN_FOLDER);
            connection = DriverManager.getConnection(CRISISWATCHER);
            initializeDatabase();
            Logger.addServerLogEntry("Base de dados iniciada com sucesso");
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao iniciar a base de dados: " + e.getMessage());
        }
    }

    public synchronized static DBManager getInstance() {
        if (instance == null) instance = new DBManager();

        return instance;
    }

    public synchronized boolean insertUser(String username, String password, int profile) {
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

            return false;
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao inserir um utilizador: " + e.getMessage());

            return false;
        }
    }

    public synchronized String getUser(String username, String password) {
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

    public synchronized boolean deleteUser(String username) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM users WHERE username = ?");
            ResultSet userResultSet = getUser(username);
            if (userResultSet != null && userResultSet.next()) {
                statement.setString(1, username);
                statement.executeUpdate();
                Logger.addServerLogEntry("O utilizador " + username + " foi eliminado com sucesso");

                return true;
            }
            Logger.addServerLogEntry("O utilizador " + username + " não foi eliminado");

            return false;
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao eliminar o utilizador " + username + ": " + e.getMessage());
            return false;
        }
    }

    private void initializeDatabase() throws SQLException {
        Statement statement = connection.createStatement();
        if (!checkTable("users")) 
            createTable(statement, "users", "uuid INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, profile INTEGER");
    }

    private boolean checkTable(String name) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet resultSet = metaData.getTables(null, null, name, new String[]{"TABLE"});

        return resultSet.next();
    }

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

            return null;
        }
    }
}
