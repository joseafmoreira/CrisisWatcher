package dev.crisiswatcher.server.model;

/**
 * Request model containing a message's {@link #uuid}, {@link #requestLevel} and {@link #approved}. <p>
 * 
 * The operations available for this {@code RequestModel} include: <p>
 * <ul>
 *  <li>{@link #getUuid()}: Returns this request's uuid</li>
 *  <li>{@link #setUuid(int)}: Sets the uuid for this request</li>
 *  <li>{@link #getRequestLevel()}: Returns this request's requestLevel</li>
 *  <li>{@link #setRequestLevel(RequestLevel)}: Sets the requestLevel for this request</li>
 *  <li>{@link #isApproved()}: Returns this request's approved</li>
 *  <li>{@link #setApproved(boolean)}: Sets the approved flag for this request</li>
 * </ul>
 * 
 * <h3>RoomModel</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 */
public class RequestModel {
    private int uuid;
    private RequestLevel requestLevel;
    private Boolean approved;

    public int getUuid() {
        return uuid;
    }

    public synchronized void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public RequestLevel getRequestLevel() {
        return requestLevel;
    }

    public synchronized void setRequestLevel(RequestLevel request) {
        this.requestLevel = request;
    }

    public Boolean isApproved() {
        return approved;
    }

    public synchronized void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public enum RequestLevel {
        EVAC(2, "EVAC"),
        COMM(1, "COMM"),
        RES(0, "RES");
        
    
        private final int key;
        private final String value;
    
        private RequestLevel(int key, String value) {
            this.key = key;
            this.value = value;
        }
    
        public static RequestLevel getEnum(String input) {
            RequestLevel request = null;
            request = getByKey(input);
            if (request == null) request = getByValue(input);
    
            return request;
        }
    
        public int getKey() {
            return key;
        }
    
        public String getValue() {
            return value;
        }
    
        @Override
        public String toString() {
            return getValue();
        }
    
        private static RequestLevel getByKey(String key) {
            return switch (key) {
                case "0" -> RequestLevel.RES;
                case "1" -> RequestLevel.COMM;
                case "2" -> RequestLevel.EVAC;
                default -> null;
            };
        }
    
        private static RequestLevel getByValue(String value) {
            value = value.toUpperCase();
            return switch (value) {
                case "CIVILIAN" -> RequestLevel.RES;
                case "COMM" -> RequestLevel.COMM;
                case "EVAC" -> RequestLevel.EVAC;
                default -> null;
            };
        }
    }
}
