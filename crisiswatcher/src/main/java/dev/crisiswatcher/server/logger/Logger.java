package dev.crisiswatcher.server.logger;

import java.time.LocalDateTime;

import dev.crisiswatcher.server.file.FileHandler;


/**
 * Abstract class used to manage server's logs. <p>
 * 
 * The operations available for this {@code Logger} include: <p>
 * <ul>
 *  <li>{@link #addServerLogEntry(String)}: Adds an entry to the server log</li>
 *  <li>{@link #addLogEntry(String, String)}: Adds an entry to a log file</li>
 * </ul>
 * 
 * <h3>Logger</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 */
public abstract class Logger {
    /**
     * Logs entry folder
     */
    private static final String ENTRY_FOLDER = "logs/";
    /**
     * Server logs file location
     */
    private static final String SERVER_FILE = ENTRY_FOLDER + "server.log";

    /**
     * Adds an entry to the server log.
     * 
     * @param message the specified message
     */

    public static void addServerLogEntry(String message) {
        addLogEntry(SERVER_FILE, message);
    }


    /**
     * Adds an entry to a log file.
     * 
     * @param path the specified path
     * @param message the specified message
     */

    private static void addLogEntry(String path, String message) {
        FileHandler.createFile(path);
        FileHandler.appendFile(path, "[" + LocalDateTime.now() + "]: " + message);
    }
}
