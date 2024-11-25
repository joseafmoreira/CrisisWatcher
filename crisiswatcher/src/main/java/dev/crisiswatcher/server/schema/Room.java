package dev.crisiswatcher.server.schema;

public class Room {
    private int uuid;
    private String name;
    private int owner;
    private String address;
    private int port;
    private String code;

    public void Room(){
        
    }

    public void Room(String name, int owner, String address, int port, String code){
        this.name = name;
        this.owner = owner;
        this.address = address;
        this.port = port;
        this.code = code;
    }


    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
