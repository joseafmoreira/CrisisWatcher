package dev.crisiswatcher.server.connection.udp;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import dev.crisiswatcher.server.manager.Manager;
import dev.crisiswatcher.server.model.RoomModel;

public class UDPConnectionHandler extends Thread {
    private List<UDPConnection> connections;

    public UDPConnectionHandler() {
        connections = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void run() {
        while (true) {
            checkConnections();
            try {
                ResultSet resultSet = (Manager.getInstance()).getRooms();
                if (resultSet != null) {
                    while (resultSet.next()) {
                        boolean found = false;
                        for (UDPConnection connection : connections) {
                            if (connection.getRoomName().equals(resultSet.getString(2))) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            connections.add(new UDPConnection(new RoomModel(resultSet.getString(2), InetAddress.getByName(resultSet.getString(3)), resultSet.getInt(4), resultSet.getString(5))));
                        }
                    }
                }
            } catch (SQLException | IOException ignored) {}
            try {
                sleep(500);
            } catch (InterruptedException ignored) {}
        }
    }

    public void checkConnections() {
        Iterator<UDPConnection> connectionsIterator = connections.iterator();
        while (connectionsIterator.hasNext()) {
            UDPConnection connection = connectionsIterator.next();
            if (!connection.isAlive()) connection.start();
        }
    }
}
