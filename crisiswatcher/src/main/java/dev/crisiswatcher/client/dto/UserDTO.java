package dev.crisiswatcher.client.dto;

public class UserDTO {
    private String name;
    private UserProfile profile;

    public boolean isLogged() {
        return !(name == null && profile == null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "Nome: " + name + "\n" + 
               "Perfil: " + profile;
    }

    public enum UserProfile {
        HIGH(3, "Alto"),
        MEDIUM(2, "Médio"),
        LOW(1, "Baixo"),
        CIVILIAN(0, "Civil");

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
