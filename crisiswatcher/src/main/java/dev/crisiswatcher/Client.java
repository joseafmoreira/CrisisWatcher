package dev.crisiswatcher;

import dev.crisiswatcher.client.io.IOSharedResources;
import dev.crisiswatcher.client.io.input.StandardInput;
import dev.crisiswatcher.client.tcp.TCPHandler;
import dev.crisiswatcher.client.udp.UDPHandler;

public class Client {
    private IOSharedResources ioSharedResources;
    private StandardInput standardInput;
    private TCPHandler tcpHandler;
    private UDPHandler udpHandler;

    public Client() {
        ioSharedResources = new IOSharedResources();
        standardInput = new StandardInput(ioSharedResources);
        tcpHandler = new TCPHandler(ioSharedResources);
        udpHandler = new UDPHandler(ioSharedResources);
    }

    public void start() {
        standardInput.start();
        if (!tcpHandler.isInterrupted() && !udpHandler.isInterrupted()) {
            tcpHandler.start();
            udpHandler.start();
            System.out.println("Bem-vindo ao CrisisWatcher!\nCaso seja necessário, digite /help para obter a lista de comandos disponíveis");
            while (true) 
                if (tcpHandler.isInterrupted() || udpHandler.isInterrupted()) System.exit(0);
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        (new Client()).start();
    }
}
