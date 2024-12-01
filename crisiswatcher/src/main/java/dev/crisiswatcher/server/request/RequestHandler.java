package dev.crisiswatcher.server.request;

import java.net.MulticastSocket;
import java.util.List;

import dev.crisiswatcher.server.model.RequestModel;

public class RequestHandler extends Thread {
    private RequestModel requestModel;
    private List<MulticastSocket> multicastSockets;

    public RequestHandler(List<MulticastSocket> multicastSockets) {
        
    }
}
