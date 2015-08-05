package ru.linachan.yggdrasil;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

public class YggdrasilAgentServer implements Runnable {

    private YggdrasilCore core;
    private YggdrasilUDPService service;

    private InetAddress serverHost;
    private Integer serverPort;

    private DatagramSocket serverSocket;

    public YggdrasilAgentServer(YggdrasilCore core) {
        this.core = core;

        this.core.logInfo("YggdrasilAgentServer: Initializing YggdrasilAgentServer...");

        try {
            this.serverHost = InetAddress.getByName(this.core.getConfig("YggdrasilAgentHost", "0.0.0.0"));
        } catch (UnknownHostException e) {
            this.core.logWarning("Unable to parse AgentServer host. Setting to loopback address: " + e.getMessage());
            this.serverHost = InetAddress.getLoopbackAddress();
        }
        this.serverPort = Integer.parseInt(this.core.getConfig("YggdrasilAgentPort", "1488"));

        try {
            this.serverSocket = new DatagramSocket(this.serverPort, this.serverHost);
            this.serverSocket.setBroadcast(true);
            this.core.logInfo("YggdrasilAgentServer: Start listening on " + this.serverHost.getHostAddress() + ":" + this.serverPort);
        } catch (SocketException e) {
            this.core.logWarning("Unable to create UDP socket: " + e.getMessage());
        }

        this.service = new YggdrasilUDPService(this.core);
    }

    private YggdrasilPacket recvData() {
        return service.recvData(serverSocket);
    }

    private boolean sendData(YggdrasilPacket packet, InetAddress address, Integer port) {
        return service.sendData(serverSocket, packet, address, port);
    }

    @Override
    public void run() {
        while (this.core.isRunningYggdrasil()) {
            YggdrasilPacket request = recvData();
            YggdrasilPacket response = process(request);
            sendData(response, request.address, request.port);
        }
        this.core.logInfo("YggdrasilAgentServer: finished");
    }

    private YggdrasilPacket process(YggdrasilPacket request) {
        YggdrasilPacket response = new YggdrasilPacket(request.opCode, request.subOpCode, request.token, null);
        if (request.opCode == (byte)0x00) {
            if (Arrays.equals(request.token, new byte[8])) {
                this.core.logInfo("YggdrasilAgentServer: New agent found:");
                this.core.logInfo("\tIP: " + request.address.getHostAddress());
                for (String key: request.parameters.keySet()) {
                    this.core.logInfo("\t" + key + ": " + request.parameters.get(key));
                }
                response.token = new byte[8];
                new Random().nextBytes(response.token);
            }
        }
        return response;
    }
}
