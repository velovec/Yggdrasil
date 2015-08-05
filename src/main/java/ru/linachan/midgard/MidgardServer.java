package ru.linachan.midgard;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

public class MidgardServer implements  Runnable {

    private YggdrasilCore core;

    private int serverPort;
    private String serverHost;
    private ServerSocket serverSocket;

    private List<Thread> clients = new LinkedList<>();

    public MidgardServer(YggdrasilCore yggdrasilCore) throws IOException {
        this.core = yggdrasilCore;

        this.core.logInfo("MidgardServer: Initializing MidgardWebServer...");

        this.serverPort = Integer.parseInt(this.core.getConfig("MidgardHTTPPort", "8080"));
        this.serverHost = this.core.getConfig("MidgardHTTPHost", "0.0.0.0");

        this.serverSocket = new ServerSocket();

        this.core.logInfo("MidgardServer: Start listening on " + this.serverHost + ":" + String.valueOf(this.serverPort));
        this.serverSocket.bind(new InetSocketAddress(this.serverHost, this.serverPort));
        this.serverSocket.setSoTimeout(1000);
    }

    @Override
    public void run() {
        while (this.core.isRunningYggdrasil()) {
            List<Thread> threadsToRemove = new LinkedList<>();
            for (Thread client : this.clients) {
                if (!client.isAlive()) {
                    try {
                        client.join();
                    } catch (InterruptedException e) {
                        this.core.logException(e);
                    }
                    threadsToRemove.add(client);
                }
            }

            for (Thread threadToRemove : threadsToRemove) {
                clients.remove(threadToRemove);
            }

            try {
                Socket sock = this.serverSocket.accept();
                Thread client = new Thread(new MidgardClientHandler(this.core, sock));
                client.start();
                this.clients.add(client);
            } catch (SocketTimeoutException e) {
                // Do nothing
            } catch (IOException e) {
                this.core.logException(e);
            }
        }
        this.core.logInfo("MidgardServer: Finished.");
    }
}