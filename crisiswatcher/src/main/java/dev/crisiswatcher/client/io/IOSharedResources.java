package dev.crisiswatcher.client.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.crisiswatcher.client.dto.UserDTO;

/**
 * Represents a shared object containing the {@link #userDTO}, {@link #tcpOutputBuffer} and {@link #udpOutputBuffer} 
 * that will be shared between I/O threads. <p>
 * 
 * The available constructors for this {@code UserProfile} include: <p>
 * <ul>
 *  <li>{@link #IOSharedResources()}: Constructs a new IOSharedResources object</li>
 * </ul>
 * 
 * The operations available for this {@code IOSharedResources} include:
 * <ul>
 *  <li>{@link #getUserDTO()}: Returns the user stored in this {@code IOSharedResources} instance</li>
 *  <li>{@link #getTcpOutputBuffer()}: Returns the TCP output buffer stored in this {@code IOSharedResources} instance</li>
 *  <li>{@link #getUdpOutputBuffer()}: Returns the UDP output buffer stored in this {@code IOSharedResources} instance</li>
 * </ul>
 */
public class IOSharedResources {
    /**
     * The user data transfer object
     */
    private UserDTO userDTO;
    /**
     * The TCP output buffer
     */
    private List<String> tcpOutputBuffer;
    /**
     * The UDP output buffer
     */
    private List<String> udpOutputBuffer;

    /**
     * Constructs a new IOSharedResources object.
     */
    public IOSharedResources() {
        userDTO = new UserDTO();
        tcpOutputBuffer = Collections.synchronizedList(new ArrayList<>());
        udpOutputBuffer = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Returns the user stored in this {@code IOSharedResources} instance.
     * 
     * @return the user stored in this {@code IOSharedResources} instance
     */
    public UserDTO getUserDTO() {
        return userDTO;
    }

    /**
     * Returns the TCP output buffer stored in this {@code IOSharedResources} instance.
     * 
     * @return the TCP output buffer stored in this {@code IOSharedResources} instance
     */
    public List<String> getTcpOutputBuffer() {
        return tcpOutputBuffer;
    }

    /**
     * Returns the UDP output buffer stored in this {@code IOSharedResources} instance.
     * 
     * @return the UDP output buffer stored in this {@code IOSharedResources} instance
     */
    public List<String> getUdpOutputBuffer() {
        return udpOutputBuffer;
    }
}
