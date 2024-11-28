package dev.crisiswatcher.logger;

import java.time.LocalDateTime;

import dev.crisiswatcher.file.FileHandler;

public abstract class Logger {
    private static final String ENTRY_FOLDER = "logs/";
    private static final String SERVER_FILE = ENTRY_FOLDER + "server.log";

    public static void addServerLogEntry(String message) {
        addLogEntry(SERVER_FILE, message);
    }

    private static void addLogEntry(String path, String message) {
        FileHandler.createFile(path);
        FileHandler.appendFile(path, "[" + LocalDateTime.now() + "]: " + message);
    }
}
