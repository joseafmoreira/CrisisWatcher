package dev.crisiswatcher.server.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Utility abstract class used to handle server files and folders. <p>
 * 
 * The operations available for this {@code FileHandler} include: <p>
 * <ul>
 *  <li>{@link #createFolder(File)}: Creates a folder in the current path</li>
 *  <li>{@link #createFile(String)}: Creates a file in the specified path</li>
 *  <li>{@link #appendFile(String, String)}: Appends a message to the top of a specified file</li>
 *  <li>{@link #createFolder(File)}: Creates a folder using a File instance</li>
 *  <li>{@link #readFile(String)}: Returns the lines of a file</li>
 * </ul>
 * 
 * <h3>FileHandler</h3>
 * @since 1.0
 * @version 1.0
 * @author CrisisWatcher
 */
public abstract class FileHandler {
    /**
     * Creates a folder in the specified path.
     * 
     * @param path the specified path
     */

    public static void createFolder(String path) {
        File folder = new File(path);
        createFolder(folder);
    }

    /**
     * Creates a file in the specified path.
     * 
     * @param path the specified path
     */

    public static void createFile(String path) {
        File file = new File(path);
        createFolder(file.getParentFile());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
        }
    }


    /**
     * Appends a message to the top of a specified file.
     * 
     * @param path the specified file's path
     * @param message the specified message
     */

    public static void appendFile(String path, String message) {
        List<String> lines = readFile(path);
        if (lines != null) {
            lines.add(0, message);
            try {
                Files.write(Paths.get(path), lines, StandardCharsets.UTF_8);
            } catch (IOException ignored) {}
        }
    }


    /**
     * Creates a folder using a File instance.
     * 
     * @param folder the specified folder File instance
     */

    private static void createFolder(File folder) {
        if (!folder.exists()) folder.mkdirs();
    }


    /**
     * Returns the lines of a file.
     * 
     * @param path the specified path
     * @return the lines of a file
     */

    private static List<String> readFile(String path) {
        try {
            return Files.readAllLines(Paths.get(path));
        } catch (IOException ignored) {}

        return null;
    }
}
