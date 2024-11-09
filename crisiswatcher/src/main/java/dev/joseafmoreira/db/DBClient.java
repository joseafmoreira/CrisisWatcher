package dev.joseafmoreira.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class DBClient {
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("localhost", 1433);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException ignored) {}
    }
}
