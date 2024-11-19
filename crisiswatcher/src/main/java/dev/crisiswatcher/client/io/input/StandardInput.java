package dev.crisiswatcher.client.io.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import dev.crisiswatcher.client.io.IOSharedResources;

public class StandardInput extends Thread {
    private IOSharedResources ioSharedResources;
    private BufferedReader stdInput;

    public StandardInput(IOSharedResources ioSharedResources) {
        this.ioSharedResources = ioSharedResources;
        stdInput = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        String input;
        try {
            while ((input = stdInput.readLine()) != null) 
                ioSharedResources.getOutputBuffer().add(input);
        } catch (IOException ignored) {
            interrupt();
        }
    }
}
