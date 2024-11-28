package dev.crisiswatcher.server.report;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import dev.crisiswatcher.server.connection.tcp.TCPConnection;
import dev.crisiswatcher.server.logger.Logger;

public class ReportHandler extends Thread {
    /**
     * Total number of log entries to be returned
     */
    private static final int NUMBER_LAST_ENTRIES = 10;
    /**
     * The server's connection list
     */
    private List<TCPConnection> connections;

    /**
     * Constructs a new ReportHandler with a specified list of TCP connections.
     * 
     * @param connections the specified list of connections
     */
    public ReportHandler(List<TCPConnection> connections) {
        this.connections = connections;
    }

    @Override
    public void run() {
        while (true) {
            String message = "Nº de conexões ativas: " + connections.size() + "\n";
            if (connections.size() != 0) {
                message += "Utilizadores ativos:\n";
                Iterator<TCPConnection> tcpConnections = connections.iterator();
                while (tcpConnections.hasNext()) {
                    TCPConnection tcpConnection = tcpConnections.next();
                    if (tcpConnection.isAlive()) message += ((tcpConnection.getUserModel().getName()) == null ? "Não autenticado" : tcpConnection.getUserModel().getName()) + "\n";
                }
            }
            message += "Últimas " + NUMBER_LAST_ENTRIES + " ações feitas no servidor:\n";
            Iterator<String> logEntries = Logger.getLastServerLogEntries(NUMBER_LAST_ENTRIES).iterator();
            while (logEntries.hasNext()) {
                message += logEntries.next() + "\n";
            }
            System.out.println("Relatório periódico\n" + message.substring(0, message.length() - 1));
            try {
                sleep(30000);
            } catch (InterruptedException ignored) {}
        }
    }
}
