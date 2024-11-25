package dev.crisiswatcher.server.schema;

public class Request {
    private int uuid;
    private RequestLevel request;
    private Boolean approved;

    public int getUuid() {
        return uuid;
    }

    public synchronized void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public RequestLevel getRequest() {
        return request;
    }

    public synchronized void setRequest(RequestLevel request) {
        this.request = request;
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

