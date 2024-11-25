package dev.crisiswatcher.server.file;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public abstract class FileHandler {
    public static void createFolder(String path) {
        File folder = new File(path);
        createFolder(folder);
    }

    public static void createFile(String path) {
        File file = new File(path);
        createFolder(file.getParentFile());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {}
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

    private static void createFolder(File folder) {
        if (!folder.exists()) folder.mkdirs();
    }

    private static List<String> readFile(String path) {
        try {
            return Files.readAllLines(Paths.get(path));
        } catch (IOException ignored) {}

        return null;
    }
}
