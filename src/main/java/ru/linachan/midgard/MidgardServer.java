package ru.linachan.midgard;

import ru.linachan.yggdrasil.service.YggdrasilService;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.List;

public class MidgardServer extends YggdrasilService {

    private int serverPort;
    private String serverHost;
    private ServerSocket serverSocket;

    private List<Thread> clients = new LinkedList<>();

    @Override
    public void run() {
        while (core.isRunningYggdrasil()) {
            List<Thread> threadsToRemove = new LinkedList<>();
            for (Thread client : this.clients) {
                if (!client.isAlive()) {
                    try {
                        client.join();
                    } catch (InterruptedException e) {
                        core.logException(e);
                    }
                    threadsToRemove.add(client);
                }
            }

            for (Thread threadToRemove : threadsToRemove) {
                clients.remove(threadToRemove);
            }

            try {
                Socket sock = this.serverSocket.accept();
                Thread client = new Thread(new MidgardClientHandler(core, sock));
                client.start();
                this.clients.add(client);
            } catch (SocketTimeoutException e) {
                // Do nothing
            } catch (IOException e) {
                core.logException(e);
            }
        }
        core.logInfo("MidgardServer: Finished.");
    }

    @Override
    protected void onInit() {
        core.logInfo("MidgardServer: Initializing MidgardWebServer...");

        this.serverPort = Integer.parseInt(core.getConfig("MidgardHTTPPort", "8080"));
        this.serverHost = core.getConfig("MidgardHTTPHost", "0.0.0.0");

        try {
            this.serverSocket = new ServerSocket();

            core.logInfo("MidgardServer: Start listening on " + this.serverHost + ":" + String.valueOf(this.serverPort));
            this.serverSocket.bind(new InetSocketAddress(this.serverHost, this.serverPort));
            this.serverSocket.setSoTimeout(1000);
        } catch (IOException e) {
            core.logException(e);
        }
    }

    @Override
    protected void onShutdown() {

    }
}