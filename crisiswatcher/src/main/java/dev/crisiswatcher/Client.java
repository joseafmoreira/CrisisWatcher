package dev.crisiswatcher;

import dev.crisiswatcher.client.io.IOSharedResources;
import dev.crisiswatcher.client.io.input.StandardInput;
import dev.crisiswatcher.client.tcp.TCPHandler;

public class Client {
    private IOSharedResources ioSharedResources;
    private StandardInput standardInput;
    private TCPHandler tcpHandler;

    public Client() {
        ioSharedResources = new IOSharedResources();
        standardInput = new StandardInput(ioSharedResources);
        tcpHandler = new TCPHandler(ioSharedResources);
    }

    public void start() {
        standardInput.start();
        if (!tcpHandler.isInterrupted()) {
            tcpHandler.start();
            System.out.println("Bem-vindo ao CrisisWatcher!\nCaso seja necessário, digite /help para obter a lista de comandos disponíveis");
            while (true) 
                if (tcpHandler.isInterrupted()) System.exit(0);
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        (new Client()).start();
    }
}
