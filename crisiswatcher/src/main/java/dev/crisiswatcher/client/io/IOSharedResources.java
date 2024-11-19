package dev.crisiswatcher.client.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.crisiswatcher.schema.User;

public class IOSharedResources {
    private User user;
    private List<String> tcpOutputBuffer;
    private List<String> udpOutputBuffer;

    public IOSharedResources() {
        user = new User();
        tcpOutputBuffer = Collections.synchronizedList(new ArrayList<>());
        udpOutputBuffer = Collections.synchronizedList(new ArrayList<>());
    }

    public User getUser() {
        return user;
    }

    public List<String> getTcpOutputBuffer() {
        return tcpOutputBuffer;
    }

    public List<String> getUdpOutputBuffer() {
        return udpOutputBuffer;
    }
}
