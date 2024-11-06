package dev.joseafmoreira.log;

import java.time.LocalDateTime;

import dev.joseafmoreira.file.FileHandler;

public class LogsHandler {
    private static final String DB_LOGS = "logs/db.log";

    private LogsHandler() {}

    public static void addDBLogEntry(String message) {
        addLogEntry(DB_LOGS, message);
    }

    private static void addLogEntry(String path, String message) {
        FileHandler.createFile(path);
        FileHandler.appendFile(path, "[" + LocalDateTime.now() + "]: " + message);
    }
}
