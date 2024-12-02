package dev.crisiswatcher.server.model;

/**
 * Request model containing a message's {@link #uuid}, {@link #requestLevel} and {@link #approved}. <p>
 * 
 * The operations available for this {@code RequestModel} include: <p>
 * <ul>
 *  <li>{@link #getUuid()}: Returns this request's uuid</li>
 *  <li>{@link #setUuid(int)}: Sets the uuid for this request</li>
 *  <li>{@link #getRequestLevel()}: Returns this request's level</li>
 *  <li>{@link #setRequestLevel(RequestLevel)}: Sets the level for this request</li>
 *  <li>{@link #isApproved()}: Returns true if this request is approved, false otherwise</li>
 *  <li>{@link #setApproved(boolean)}: Sets the approved flag for this request</li>
 * </ul>
 * 
 * <h3>RoomModel</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 */
public class RequestModel {
    /**
     * The uuid of this request
     */
    private int uuid;
    /**
     * The requestLevel of this request
     */
    private RequestLevel level;
    /**
     * The approved flag of this request
     */
    private Boolean approved;

    /**
     * Returns this request's uuid.
     * 
     * @return this request's uuid
     */
    public int getUuid() {
        return uuid;
    }

    /**
     * Sets the uuid for this request.
     * 
     * @param uuid the uuid to be set for this request
     */
    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns this request's level.
     * 
     * @return this request's level
     */
    public RequestLevel getRequestLevel() {
        return level;
    }

    /**
     * Sets the level for this request
     * 
     * @param level the level to be set for this request
     */
    public void setRequestLevel(RequestLevel level) {
        this.level = level;
    }

    /**
     * Returns true if this request is approved, false otherwise.
     * 
     * @return true if this request is approved, false otherwise
     */
    public Boolean isApproved() {
        return approved;
    }

    /**
     * Sets the approved flag for this request.
     * 
     * @param approved the approved flag to be set for this request
     */
    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    /**
     * Represents the possible level values that this request can have. 
     * Each level is associated with a unique key and value pair. <p>
     * 
     * The available constructors for this {@code RequestLevel} include: <p>
     * <ul>
     *  <li>{@link #RequestLevel(int, string)}: Constructs a new RequestLevel with a specified key and value</li>
     * </ul>
     * 
     * The operations available for this {@code RequestLevel} include: <p>
     * <ul>
     *  <li>{@link #getEnum(String)}: Returns a {@code UserProfile} enum or null based on a given input</li>
     *  <li>{@link #getKey()}: Returns the key of this profile</li>
     *  <li>{@link #getValue()}: Returns the value of this profile</li>
     *  <li>{@link #toString()}: Returns a string representation of this profile</li>
     *  <li>{@link #getByKey(int)}: Returns a {@code UserProfile} enum of null based on a given key</li>
     *  <li>{@link #getByValue(String)}: Returns a {@code UserProfile} enum of null based on a given value</li>
     * </ul> 
     */
    public enum RequestLevel {
        /**
         * Represents an evacuation request
         */
        EVAC(2, "EVAC"),
        /**
         * Represents a communication request
         */
        COMM(1, "COMM"),
        /**
         * Represents a resources request
         */
        RES(0, "RES");
    
        /**
         * The key of this level
         */
        private final int key;
        /**
         * The value of this level
         */
        private final String value;
    
        /**
         * Constructs a new RequestLevel with a specified key and value.
         * 
         * @param key the key of this level
         * @param value the value of this level
         */
        private RequestLevel(int key, String value) {
            this.key = key;
            this.value = value;
        }
    
        /**
         * Returns a {@code RequestLevel} enum or null based on a given input.
         * 
         * @param input the specified key or value to look for
         * @return the corresponding {@code RequestLevel}, or {@code null} if no match is found
         */
        public static RequestLevel getEnum(String input) {
            RequestLevel level = null;
            try {
                level = getByKey(Integer.valueOf(input));
                if (level == null) level = getByValue(input);
            } catch (NumberFormatException ignored) {
                level = getByValue(input);
            }
    
            return level;
        }
    
        /**
         * Returns the key of this request level.
         * 
         * @return the key of this request level
         */
        public int getKey() {
            return key;
        }
    
        /**
         * Returns the value of this request level.
         * 
         * @return the value of this request level
         */
        public String getValue() {
            return value;
        }
    
        /**
         * Returns a string representation of this request level.
         * 
         * @return a string representation of this request level
         */
        @Override
        public String toString() {
            return getValue();
        }
    
        /**
         * Returns a {@code RequestLevel} enum of null based on a given key.
         * 
         * @param key the given key
         * @return a {@code RequestLevel} enum of null based on a given key
         */
        private static RequestLevel getByKey(int key) {
            return switch (key) {
                case 0 -> RequestLevel.RES;
                case 1 -> RequestLevel.COMM;
                case 2 -> RequestLevel.EVAC;
                default -> null;
            };
        }
    
        /**
         * Returns a {@code RequestLevel} enum of null based on a given value.
         * 
         * @param key the given value
         * @return a {@code RequestLevel} enum of null based on a given value
         */
        private static RequestLevel getByValue(String value) {
            return switch (value) {
                case "RES" -> RequestLevel.RES;
                case "COMM" -> RequestLevel.COMM;
                case "EVAC" -> RequestLevel.EVAC;
                default -> null;
            };
        }
    }
}
