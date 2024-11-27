package dev.crisiswatcher.client.dto;

/**
 * User data transfer object containing an user's {@link #name} and {@link #profile}. <p>
 * 
 * The operations available for this {@code UserDTO} include: <p>
 * <ul>
 *  <li>{@link #isLogged()}: Checks if this user is logged in</li>
 *  <li>{@link #getName()}: Returns this user's name</li>
 *  <li>{@link #setName()}: Sets the name for this user</li>
 *  <li>{@link #getProfile()}: Returns this user's profile</li>
 *  <li>{@link #setProfile()}: Sets the profile of this user</li>
 *  <li>{@link #toString()}: Returns a string representation of this user</li>
 * </ul> 
 * 
 * <h3>UserDTO</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 */
public class UserDTO {
    /**
     * The name of this user
     */
    private String name;
    /**
     * The profile of this user
     */
    private UserProfile profile;

    /**
     * Checks if this user is logged in.
     * 
     * @return true if this user is logged in, false otherwise
     */
    public boolean isLogged() {
        return !(name == null && profile == null);
    }

    /**
     * Returns this user's name-
     * 
     * @return this user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for this user.
     * 
     * @param name the name to be set for this user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns this user's profile.
     * 
     * @return this user's profile
     */
    public UserProfile getProfile() {
        return profile;
    }

    /**
     * Sets the profile for this user.
     * 
     * @param profile the profile to be set for this user
     */
    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

    /**
     * Returns a string representation of this user.
     * 
     * @return a string representation of this user
     */
    @Override
    public String toString() {
        return "Nome: " + name + "\n" + 
               "Perfil: " + profile;
    }

    /**
     * Represents the possible profile values that this user can have. <p>
     * Each profile is associated with a unique key and value pair to categorize 
     * users based on their profiles.
     * 
     * The available constructors for this {@code UserProfile} include: <p>
     * <ul>
     *  <li>{@link #UserProfile(int, string)}: Constructs a new UserProfile with a specified key and value</li>
     * </ul>
     * 
     * The operations available for this {@code UserProfile} include: <p>
     * <ul>
     *  <li>{@link #getEnum(String)}: Returns a {@code UserProfile} enum or null based on a given input</li>
     *  <li>{@link #getKey()}: Returns the key of this profile</li>
     *  <li>{@link #getValue()}: Returns the value of this profile</li>
     *  <li>{@link #toString()}: Returns a string representation of this profile</li>
     *  <li>{@link #getByKey(int)}: Returns a {@code UserProfile} enum of null based on a given key</li>
     *  <li>{@link #getByValue(String)}: Returns a {@code UserProfile} enum of null based on a given value</li>
     * </ul> 
     */
    public enum UserProfile {
        /**
         * Represents a user with a high profile
         */
        HIGH(3, "Alto"),
        /**
         * Represents a user with a medium profile
         */
        MEDIUM(2, "Médio"),
        /**
         * Represents a user with a low profile
         */
        LOW(1, "Baixo"),
        /**
         * Represents a user with a civilian profile
         */
        CIVILIAN(0, "Civil");

        /**
         * The key of this profile
         */
        private final int key;
        /**
         * The value of this profile
         */
        private final String value;

        /**
         * Constructs a new UserProfile with a specified key and value.
         * 
         * @param key the key of this profile
         * @param value the value of this profile
         */
        private UserProfile(int key, String value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Returns a {@code UserProfile} enum or null based on a given input.
         * 
         * @param input the specified key or value to look for
         * @return the corresponding {@code UserProfile}, or {@code null} if no match is found
         */
        public static UserProfile getEnum(String input) {
            UserProfile userProfile = null;
            try {
                userProfile = getByKey(Integer.valueOf(input));
                if (userProfile == null) userProfile = getByValue(input);
            } catch (NumberFormatException ignored) {
                userProfile = getByValue(input);
            }

            return userProfile;
        }

        /**
         * Returns the key of this profile.
         * 
         * @return the key of this profile
         */
        public int getKey() {
            return key;
        }

        /**
         * Returns the value of this profile.
         * 
         * @return the value of this profile
         */
        public String getValue() {
            return value;
        }

        /**
         * Returns a string representation of this profile.
         * 
         * @return a string representation of this profile
         */
        @Override
        public String toString() {
            return getValue();
        }

        /**
         * Returns a {@code UserProfile} enum of null based on a given key.
         * 
         * @param key the given key
         * @return a {@code UserProfile} enum of null based on a given key
         */
        private static UserProfile getByKey(int key) {
            return switch (key) {
                case 0 -> UserProfile.CIVILIAN;
                case 1 -> UserProfile.LOW;
                case 2 -> UserProfile.MEDIUM;
                case 3 -> UserProfile.HIGH;
                default -> null;
            };
        }

        /**
         * Returns a {@code UserProfile} enum of null based on a given value.
         * 
         * @param key the given value
         * @return a {@code UserProfile} enum of null based on a given value
         */
        private static UserProfile getByValue(String value) {
            return switch (value) {
                case "Civil" -> UserProfile.CIVILIAN;
                case "Baixo" -> UserProfile.LOW;
                case "Médio" -> UserProfile.MEDIUM;
                case "Alto" -> UserProfile.HIGH;
                default -> null;
            };
        }
    }
}
