package dev.crisiswatcher.server.connection.udp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UDPConnectionHandler extends Thread {
    private List<UDPConnection> connections;

    public UDPConnectionHandler() {
        connections = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void run() {
        
    }
}
