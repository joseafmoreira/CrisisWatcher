package dev.joseafmoreira.logger;

import java.time.LocalDateTime;

import dev.joseafmoreira.file.FileManager;

public abstract class LogManager {
    private static final String DB_LOGS = "logs/db.log";

    public static void addDBLogEntry(String message) {
        addLogEntry(DB_LOGS, message);
    }

    private static void addLogEntry(String path, String message) {
        FileManager.createFile(path);
        FileManager.appendFile(path, "[" + LocalDateTime.now() + "]: " + message);
    }
}
