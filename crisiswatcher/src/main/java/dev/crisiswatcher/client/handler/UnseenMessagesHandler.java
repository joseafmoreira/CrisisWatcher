package dev.crisiswatcher.client.handler;

import java.util.List;

public class UnseenMessagesHandler extends Thread {
    private List<String> tcpOutputBuffer;
    private boolean running;

    public UnseenMessagesHandler(List<String> tcpOutputBuffer) {
        this.tcpOutputBuffer = tcpOutputBuffer;
        running = false;
    }

    @Override
    public void run() {
        while (true) {
            if (running && !tcpOutputBuffer.contains("/unseen")) 
                tcpOutputBuffer.add("/unseen");
            try {
                sleep(500);
            } catch (InterruptedException ignored) {}
        }
    }

    public void setRunningState(boolean running) {
        this.running = running;
    }
}
