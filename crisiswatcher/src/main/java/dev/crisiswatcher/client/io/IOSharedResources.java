package dev.crisiswatcher.client.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.crisiswatcher.client.dto.UserDTO;

public class IOSharedResources {
    private UserDTO userDTO;
    private List<String> tcpOutputBuffer;
    private List<String> udpOutputBuffer;

    public IOSharedResources() {
        userDTO = new UserDTO();
        tcpOutputBuffer = Collections.synchronizedList(new ArrayList<>());
        udpOutputBuffer = Collections.synchronizedList(new ArrayList<>());
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public List<String> getTcpOutputBuffer() {
        return tcpOutputBuffer;
    }

    public List<String> getUdpOutputBuffer() {
        return udpOutputBuffer;
    }
}
