package dev.crisiswatcher.client.dto;

import java.net.InetAddress;

/**
 * Room data transfer object containing a room's {@link #name}, {@link #ip} and {@link #port}. <p>
 * 
 * The available constructors for this {@code RoomDTO} include: <p>
 * <ul>
 *  <li>{@link #RoomDTO()}: Constructs an empty RoomDTO</li>
 *  <li>{@link #RoomDTO(RoomDTO)}: Constructs a new RoomDTO based on another RoomDTO instance</li>
 * </ul>
 * 
 * The operations available for this {@code UserDTO} include: <p>
 * <ul>
 *  <li>{@link #getName()}: Returns this room's name</li>
 *  <li>{@link #setName()}: Sets the name for this room</li>
 *  <li>{@link #getIp()}: Returns this room's ip</li>
 *  <li>{@link #setIp()}: Sets the ip of this room</li>
 *  <li>{@link #getPort()}: Returns this room's port</li>
 *  <li>{@link #setPort()}: Sets the port of this room</li>
 *  <li>{@link #equals(Object)}: Compares this room data transfer object with the specified object for equality</li>
 *  <li>{@link #toString()}: Returns a string representation of this room</li>
 * </ul> 
 * 
 * <h3>RoomDTO</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 */
public class RoomDTO {
    /**
     * The name of this room
     */
    private String name;
    /**
     * The ip address of this room
     */
    private InetAddress ip;
    /**
     * The port of this room
     */
    private int port;

    /**
     * Constructs an empty RoomDTO.
     */
    public RoomDTO() {}

    /**
     * Constructs a new RoomDTO based on another RoomDTO instance.
     * 
     * @param roomDTO the specified roomDTO instance
     */
    public RoomDTO(RoomDTO roomDTO) {
        name = roomDTO.name;
        ip = roomDTO.ip;
        port = roomDTO.port;
    }

    public boolean isValid() {
        return (name != null && ip != null && port != 0);
    }

    /**
     * Returns this room's name.
     * 
     * @return this room's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for this room.
     * 
     * @param name the specified name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns this room's ip.
     * 
     * @return this room's ip
     */
    public InetAddress getIp() {
        return ip;
    }

    /**
     * Sets the ip of this room.
     * 
     * @param ip the specified ip
     */
    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    /**
     * Returns this room's port.
     * 
     * @return this room's port
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port of this room.
     * 
     * @param port the specified port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Compares this room data transfer object with the specified object for equality.
     * 
     * @param obj the object to compare with
     * @return true if the specified object is equal to this room data transfer object, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RoomDTO other = (RoomDTO) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (port != other.port)
            return false;
        return true;
    }

    /**
     * Returns a string representation of this room.
     * 
     * @return a string representation of this room
     */
    @Override
    public String toString() {
        return name + " " + ip + " " + port;
    }
}
