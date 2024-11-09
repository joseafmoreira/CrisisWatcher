package dev.joseafmoreira.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public abstract class FileManager {
    public static boolean checkFolder(String path) {
        return (new File(path)).exists();
    }

    public static void createFolder(String path) {
        File folder = new File(path);
        if (!folder.exists()) folder.mkdirs();
    }

    public static void createFile(String path) {
        File file = new File(path);
        createFolder(file.getParentFile().toString());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
        }
    }

    public static List<String> readFile(String path) {
        try {
            return Files.readAllLines(Paths.get(path));
        } catch (IOException ignored) {
            return null;
        }
    }

    public static void appendFile(String path, String message) {
        List<String> lines = readFile(path);
        if (lines != null) {
            lines.add(0, message);

            try {
                Files.write(Paths.get(path), lines, StandardCharsets.UTF_8);
            } catch (IOException ignored) {}
        }
    }
}
