package dev.crisiswatcher.client.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketOutput extends Thread {
    private BufferedReader stdInput;
    private PrintWriter socketOutput;

    public SocketOutput(Socket clientSocket) throws IOException {
        stdInput = new BufferedReader(new InputStreamReader(System.in));
        socketOutput = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        String input;
        try {
            while ((input = stdInput.readLine()) != null) {
                socketOutput.println(input);
            }
        } catch (IOException ignored) {
            interrupt();
        }
    }
}
