package dev.crisiswatcher.server.model;

/**
 * Message model containing a message's {@link #uuid}, {@link #chatRoomId} and {@link #content}. <p>
 * 
 * The operations available for this {@code RoomModel} include: <p>
 * <ul>
 *  <li>{@link #getUuid()}: Returns this message's uuid</li>
 *  <li>{@link #setUuid(int)}: Sets the uuid for this message</li>
 *  <li>{@link #getChatRoomId()}: Returns this message's chatRoomId</li>
 *  <li>{@link #setChatRoomId(int)}: Sets the chatRoomId for this message</li>
 *  <li>{@link #getContent()}: Returns this message's content</li>
 *  <li>{@link #setContent(String)}: Sets the content for this message</li>
 * </ul>
 * 
 * <h3>RoomModel</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 */
public class MessageModel {
    /**
     * The uuid of this message
     */
    private int uuid;
    /**
     * The chatRoomId of this message
     */
    private int chatRoomId;
    /**
     * The content of this message
     */
    private String content;
    
    /**
     * Returns this message's uuid.
     * 
     * @return this message's uuid
     */
    public int getUuid() {
        return uuid;
    }

    /**
     * Sets the uuid for this message.
     * 
     * @param uuid the uuid to be set for this message
     */
    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns this message's chatRoomId.
     * 
     * @return this message's chatRoomId
     */
    public int getChatRoomId() {
        return chatRoomId;
    }

    /**
     * Sets the chatRoomId for this message.
     * 
     * @param chatRoomId the chatRoomId to be set for this message
     */
    public void setChatRoomId(int chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    /**
     * Returns this message's content.
     * 
     * @return this message's content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content for this message.
     * 
     * @param content the content to be set for this message
     */
    public void setContent(String content) {
        this.content = content;
    }
}
