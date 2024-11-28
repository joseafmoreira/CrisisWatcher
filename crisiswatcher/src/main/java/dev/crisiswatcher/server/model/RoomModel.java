package dev.crisiswatcher.server.model;

import java.net.InetAddress;

/**
 * Room model containing a room's {@link #uuid}, {@link #name}, {@link #owner}, {@link #ip}, {@link #port} and {@link #code}. <p>
 * 
 * The operations available for this {@code RoomModel} include: <p>
 * <ul>
 *  <li>{@link #getUuid()}: Returns this room's uuid</li>
 *  <li>{@link #setUuid(int)}: Sets the uuid for this room</li>
 *  <li>{@link #getName()}: Returns this room's name</li>
 *  <li>{@link #setName(String)}: Sets the name for this room</li>
 *  <li>{@link #getOwner()}: Returns this room's owner</li>
 *  <li>{@link #setOwner(int)}: Sets the owner for this room</li>
 *  <li>{@link #getIp()}: Returns this room's ip</li>
 *  <li>{@link #setIp(InetAddress)}: Sets the ip for this room</li>
 *  <li>{@link #getPort()}: Returns this room's port</li>
 *  <li>{@link #setPort(String)}: Sets the port for this room</li>
 *  <li>{@link #getCode()}: Returns this room's code</li>
 *  <li>{@link #setCode(String)}: Sets the code for this room</li>
 * </ul>
 * 
 * <h3>RoomModel</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 */
public class RoomModel {
    /**
     * The uuid of this room
     */
    private int uuid;
    /**
     * The name of this room
     */
    private String name;
    /**
     * The owner of this room
     */
    private int owner;
    /**
     * The ip of this room
     */
    private InetAddress ip;
    /**
     * The port of this room
     */
    private int port;
    /**
     * The code of this room
     */
    private String code;

    /**
     * Returns this room's uuid.
     * 
     * @return this room's uuid
     */
    public int getUuid() {
        return uuid;
    }

    /**
     * Sets the uuid for this room.
     * 
     * @param uuid the uuid to be set for this room
     */
    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    /**
     * 
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * 
     * @return
     */
    public int getOwner() {
        return owner;
    }

    /**
     * 
     * 
     * @param owner
     */
    public void setOwner(int owner) {
        this.owner = owner;
    }

    /**
     * 
     * 
     * @return
     */
    public InetAddress getIp() {
        return ip;
    }

    /**
     * 
     * 
     * @param ip
     */
    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    /**
     * 
     * 
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * 
     * 
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 
     * 
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * 
     * 
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }
}
