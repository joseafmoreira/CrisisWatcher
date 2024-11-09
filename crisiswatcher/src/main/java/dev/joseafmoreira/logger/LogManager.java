package dev.joseafmoreira.logger;

import java.time.LocalDateTime;

import dev.joseafmoreira.file.FileManager;

public class LogManager {
    private static final String DB_LOGS = "logs/db.log";

    private LogManager() {}

    public static void addDBLogEntry(String message) {
        addLogEntry(DB_LOGS, message);
    }

    private static void addLogEntry(String path, String message) {
        FileManager.createFile(path);
        FileManager.appendFile(path, "[" + LocalDateTime.now() + "]: " + message);
    }
}
