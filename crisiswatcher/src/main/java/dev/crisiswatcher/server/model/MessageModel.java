package dev.crisiswatcher.server.model;

import java.net.InetAddress;

/**
 * Message model containing a message's {@link #uuid}, {@link #chatRoomId} and {@link #content}. <p>
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
public class MessageModel {
    private int uuid;
    private int chatRoomId;
    private String content;

    
}
