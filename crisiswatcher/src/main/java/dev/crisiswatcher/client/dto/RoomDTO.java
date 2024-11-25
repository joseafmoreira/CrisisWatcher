package dev.crisiswatcher.client.dto;

import java.net.InetAddress;

public class RoomDTO {
    private String name;
    private InetAddress address;
    private int port;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public InetAddress getAddress() {
        return address;
    }
    public void setAddress(InetAddress address) {
        this.address = address;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }    
}
