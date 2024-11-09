package dev.joseafmoreira.db.server.manager;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import dev.joseafmoreira.file.FileManager;
import dev.joseafmoreira.logger.LogManager;

public class Manager {
    private static final String SQLITE_PARAMS = "jdbc:sqlite:";
    private static final String DB_FOLDER = "db";
    private static final String USERS_DB = SQLITE_PARAMS + DB_FOLDER + "/users.db";
    private static Manager instance;
    private boolean writing;

    private Manager() {
        initializeDatabase();
        writing = false;
    }

    public static Manager getInstance() {
        if (instance == null) instance = new Manager();

        return instance;
    }

    private void initializeDatabase() {
        FileManager.createFolder(DB_FOLDER);
        try {
            Connection connection = DriverManager.getConnection(USERS_DB);
            Statement statement = connection.createStatement();

            if (!checkTable(connection, "users")) createUsersTable(statement);
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
    }

    private void createUsersTable(Statement statement) {
        createTable(statement, "users", "uuid INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, profile INTEGER");
    }

    private boolean checkTable(Connection connection, String name) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, name, new String[] {"TABLE"});

        return resultSet.next();
    }

    private void createTable(Statement statement, String name, String parameters) {
        try {
            statement.execute("CREATE TABLE " + name + " (" + parameters + ")");
            LogManager.addDBLogEntry("A tabela " + name + " foi criada");
        } catch (SQLException ignored) {
            LogManager.addDBLogEntry("Erro ao criar a tabela " + name);
        }
    }

    public void loginUser(String username, String password) {
        try {
            Connection connection = DriverManager.getConnection(USERS_DB);
            Statement statement = connection.createStatement();

            
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
    }
}
