package dev.crisiswatcher.protocol;

public abstract class CloseProtocol {
    public static String processInput(String input) {
        String output = null;
        if (input.equalsIgnoreCase("/close")) 
            output = "/close";

        return output;
    }
}
