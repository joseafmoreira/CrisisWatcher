package dev.crisiswatcher.protocol;

public abstract class UserSettingsProtocol {
    public static String processInput(String input) {
        String lowerInput = input.toLowerCase();
        String output = null;
        if (lowerInput.contains("/username")) {
            output = changeUsername(input);
        } else if (lowerInput.contains("/password")) {
            output = changePassword(input);
        }

        return output;
    }

    private static String changeUsername(String input) {

    }

    private static String changePassword(String input) {
        
    }
}
