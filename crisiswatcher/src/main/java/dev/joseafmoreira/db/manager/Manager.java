package dev.joseafmoreira.db.manager;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import dev.joseafmoreira.file.FileHandler;
import dev.joseafmoreira.log.LogsHandler;

public class Manager {
    private static final String SQLITE_PARAMS = "jdbc:sqlite:";
    private static final String DB_FOLDER = "db";
    private static final String USERS_DB = SQLITE_PARAMS + DB_FOLDER + "/users.db";
    private static Manager instance;

    private Manager() {}

    public static Manager getInstance() {
        if (instance == null) instance = new Manager();

        return instance;
    }

    public void initializeDatabase() {
        FileHandler.createFolder(DB_FOLDER);
        try {
            Connection connection = DriverManager.getConnection(USERS_DB);
            Statement statement = connection.createStatement();

            if (!checkTable(connection, "users")) createUsersDB(statement);
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
    }

    private boolean checkTable(Connection connection, String name) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, name, new String[] {"TABLE"});

        return resultSet.next();
    }

    private void createUsersDB(Statement statement) {
        createTable(statement, "users", "uuid INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, profile INTEGER");
    }

    private void createTable(Statement statement, String name, String parameters) {
        try {
            statement.execute("CREATE TABLE " + name + " (" + parameters + ")");
            LogsHandler.addDBLogEntry("A tabela " + name + " foi criada");
        } catch (SQLException ignored) {
            LogsHandler.addDBLogEntry("Erro ao criar a tabela " + name);
        }
    }

    private void checkUser(String username) {
        
    }
}
