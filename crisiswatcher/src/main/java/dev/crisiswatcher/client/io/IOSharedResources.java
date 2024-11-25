package dev.crisiswatcher.client.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.crisiswatcher.client.dto.RoomDTO;
import dev.crisiswatcher.client.dto.UserDTO;
import dev.crisiswatcher.client.udp.UDPHandler;

public class IOSharedResources {
    private UserDTO userDTO;
    private RoomDTO roomDTO;
    private UDPHandler udpHandler;
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

    public RoomDTO getRoomDTO() {
        return roomDTO;
    }

    public UDPHandler getUdpHandler() {
        return udpHandler;
    }

    public void setUdpHandler(UDPHandler udpHandler) {
        this.udpHandler = udpHandler;
    }

    public List<String> getTcpOutputBuffer() {
        return tcpOutputBuffer;
    }

    public List<String> getUdpOutputBuffer() {
        return udpOutputBuffer;
    }
}
