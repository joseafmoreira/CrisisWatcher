package dev.crisiswatcher.schema;

public class User {
    private int uuid;
    private String username;
    private UserProfile profile;

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    private enum UserProfile {
        HIGH(3, "High"),
        MEDIUM(2, "Medium"),
        LOW(1, "Low"),
        CIVILIAN(0, "Civilian");

        private final int key;
        private final String value;

        private UserProfile(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public static UserProfile getEnum(String input) {
            UserProfile userProfile = null;
            userProfile = getByKey(input);
            if (userProfile == null) userProfile = getByValue(input);

            return userProfile;
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

        private static UserProfile getByKey(String key) {
            return switch (key) {
                case "0" -> UserProfile.CIVILIAN;
                case "1" -> UserProfile.LOW;
                case "2" -> UserProfile.MEDIUM;
                case "3" -> UserProfile.HIGH;
                default -> null;
            };
        }

        private static UserProfile getByValue(String value) {
            value = value.toUpperCase();
            return switch (value) {
                case "CIVILIAN" -> UserProfile.CIVILIAN;
                case "LOW" -> UserProfile.LOW;
                case "MEDIUM" -> UserProfile.MEDIUM;
                case "HIGH" -> UserProfile.HIGH;
                default -> null;
            };
        }
    }
}
