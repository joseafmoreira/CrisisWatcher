package dev.crisiswatcher.server.manager;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import dev.crisiswatcher.server.file.FileHandler;
import dev.crisiswatcher.server.logger.Logger;
import dev.crisiswatcher.server.protocol.RoomProtocol;
import dev.crisiswatcher.server.schema.Message;
import dev.crisiswatcher.server.schema.Request;
import dev.crisiswatcher.server.schema.Request.RequestLevel;
import dev.crisiswatcher.server.schema.Room;

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

                int id = getUser(username).getInt(1);

                boolean result = insertUserToProfileRoom(id, profile);

                if(!result){
                    Logger.addServerLogEntry("Erro ao adicionar o user a sua respetiva room");
                    return false;
                }

                return true;
            }
            Logger.addServerLogEntry("O utilizador " + username + " já existe");

            return false;
        } catch (SQLException e) {
            Logger.addServerLogEntry("Erro ao inserir um utilizador: " + e.getMessage());

            return false;
        }
    }

    private synchronized boolean insertUserToProfileRoom(int id, int profile) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO rommEntries (user,room) VALUES (?, ?)");
            int high = getRoomId("HIGHROOM");
            int medium = getRoomId("MEDIUMROOM");
            int low = getRoomId("LOWROOM");
            int civ = getRoomId("GENERALROOM");

            if(profile == 3){
                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, high);
                preparedStatement.executeUpdate();

                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, medium);
                preparedStatement.executeUpdate();

                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, low);
                preparedStatement.executeUpdate();

                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, civ);
                preparedStatement.executeUpdate();
                return true;
            }else if(profile == 2){
                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, medium);
                preparedStatement.executeUpdate();

                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, low);
                preparedStatement.executeUpdate();

                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, civ);
                preparedStatement.executeUpdate();
                return true;
            }else if(profile == 1){
                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, low);
                preparedStatement.executeUpdate();

                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, civ);
                preparedStatement.executeUpdate();
                return true;
            }else if(profile == 0){
                preparedStatement.setInt(1, id);
                preparedStatement.setInt(2, civ);
                preparedStatement.executeUpdate();
                return true;
            }else{
                Logger.addServerLogEntry("Erro profile invalido: ");
                return false;
            }

        } catch (Exception e) {
            Logger.addServerLogEntry("Erro ao adicionar o User nas suas respetivas default rooms: " +e.getMessage());
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

    public synchronized boolean insertMessage(Message message){
        int chatRoomId = message.getChatRoomId();
        String content = message.getContent();
        
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO messages (chatRoomId, content) VALUES (?, ?)");
           
            preparedStatement.setInt(1, chatRoomId);
            preparedStatement.setString(2, content);

            preparedStatement.executeUpdate();
            Logger.addServerLogEntry("Uma mensagem foi inserido com sucesso");

            return true;
            
            
        } catch (Exception e) {
            Logger.addServerLogEntry("Erro ao inserir uma mensagem" + e.getMessage());
            return false;
        }
    }

    public synchronized List<Message> getMessagesByRoomId(int roomId){
        List<Message> messages = new ArrayList<>();

        try {
            
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM messages WHERE chatRoomID = ?");
           
            preparedStatement.setInt(1, roomId);

            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Message message = new Message();

                message.setUuid(resultSet.getInt(1));
                message.setChatRoomId(resultSet.getInt(2));
                message.setContent(resultSet.getString(3));

                messages.add(message);
            }
            Logger.addServerLogEntry("Mensagens foram retornadas com sucesso");

            return messages;
        } catch (Exception e) {
            Logger.addServerLogEntry("Erro ao encontrar mensagens por room: "+ e.getMessage());
            return null;
        }
    }

    public synchronized boolean insertRoom(Room room){
        String name = room.getName();
        int owner = room.getOwner();
        String address = room.getAddress();
        int port = room.getPort();
        String code = room.getCode();

        
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO rooms (name, owner, address, port, code) VALUES (?, ?, ?, ?, ?)");
           
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, owner);
            preparedStatement.setString(3, address);
            preparedStatement.setInt(4, port);
            preparedStatement.setString(5, code);
            

            preparedStatement.executeUpdate();
            Logger.addServerLogEntry("Uma sala foi criada com sucesso");

            return true;
        } catch (Exception e) {
            Logger.addServerLogEntry("Erro ao criar uma sala: " + e.getMessage());
            return false;
        }

    }

    public synchronized boolean insertRequest(Request request){
        int level = request.getRequest().getKey();
        boolean approved = request.isApproved();

        
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO requests (level,approved) VALUES (?, ?)");
           
            
            preparedStatement.setInt(1, level);
            preparedStatement.setBoolean(2, approved);
            

            preparedStatement.executeUpdate();
            Logger.addServerLogEntry("Um Request foi criado com sucesso");

            return true;
        } catch (Exception e) {
            Logger.addServerLogEntry("Erro ao criar um request: " + e.getMessage());
            return false;
        }

    }

    public synchronized List<Request> getRequestsbyLevel(int level){
        List<Request> requests = new ArrayList<>();

        try {
            
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM requests WHERE level = ?");
           
            preparedStatement.setInt(1, level);

            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Request request = new Request();

                request.setUuid(resultSet.getInt(1));
                request.setRequest(RequestLevel.getEnum(Integer.toString(resultSet.getInt(2))));
                request.setApproved(resultSet.getBoolean(3));

                requests.add(request);
            }
            Logger.addServerLogEntry("Requests foram retornados com sucesso");

            return requests;
        } catch (Exception e) {
            Logger.addServerLogEntry("Erro ao encontrar requests por level: "+ e.getMessage());
            return null;
        }
    }

    private void initializeDatabase() throws SQLException {
        Statement statement = connection.createStatement();
        if (!checkTable("users")) 
            createTable(statement, "users", "uuid INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, profile INTEGER");
        if(!checkTable("rooms"))
            createTable(statement, "rooms", "uuid INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, owner INTEGER, address TEXT, port INTEGER, code TEXT, FOREIGN KEY(owner) REFERENCES users(uuid)");
            boolean result = createDefaultRooms();
            if(!result){
                Logger.addServerLogEntry("Erro ao criar default rooms");;
            }
        if(!checkTable("messages"))
            createTable(statement, "messages", "uuid INTEGER PRIMARY KEY AUTOINCREMENT, chatRoomID INTEGER, Content TEXT, DateTime DATETIME, FOREIGN KEY(chatRoomID) REFERENCES rooms(uuid)");
        if(!checkTable("requests"))
            createTable(statement, "requests", "uuid INTEGER PRIMARY KEY AUTOINCREMENT, level INTEGER, approved BOOLEAN");
        if(!checkTable("roomEntries"))
            createTable(statement, "roomEntries", "uuid INTEGER PRIMARY KEY AUTOINCREMENT, user INTEGER, room INTEGER, FOREIGN KEY(user) REFERENCES users(uuid), FOREIGN KEY(room) REFERENCES rooms(uuid)");
    }

    private boolean createDefaultRooms() throws SQLException{
        Room High = new Room("High", 0, RoomProtocol.getIp(),6789, "HIGHROOM");
        Room Medium = new Room("Medium", 0,RoomProtocol.getIp(),6789,"MEDIUMROOM" );
        Room Low = new Room("Low", 0, RoomProtocol.getIp(),6789,"LOWROOM");
        Room General = new Room("General", 0 , RoomProtocol.getIp(),6789, "GENERALROOM");

        try {
            insertRoom(High);
            insertRoom(Medium);
            insertRoom(Low);
            insertRoom(General);
            return true;
        } catch (Exception e) {
            Logger.addServerLogEntry("Erro ao criar as default rooms: "+e.getMessage());
            return false;
        }
        
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

    private synchronized int getRoomId(String code){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM rooms WHERE code = ?");
           
            preparedStatement.setString(1, code);

            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Logger.addServerLogEntry("RoomID retornado com sucesso");

                return resultSet.getInt(1);
            }
            return -1;


        } catch (Exception e) {
            Logger.addServerLogEntry("Erro ao retornar RoomID: "+e.getMessage());
            return -1;
        }

    }
}
