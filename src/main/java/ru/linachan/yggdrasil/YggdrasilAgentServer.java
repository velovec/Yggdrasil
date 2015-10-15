package ru.linachan.yggdrasil;

import ru.linachan.yggdrasil.service.YggdrasilService;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class YggdrasilAgentServer extends YggdrasilService {

    private YggdrasilUDPService service;

    private InetAddress serverHost;
    private Integer serverPort;

    private DatagramSocket serverSocket;

    private YggdrasilPacket recvData() {
        return service.recvData(serverSocket);
    }

    private boolean sendData(YggdrasilPacket packet, InetAddress address, Integer port) {
        return service.sendData(serverSocket, packet, address, port);
    }

    @Override
    public void run() {
        while (core.isRunningYggdrasil()) {
            YggdrasilPacket request = recvData();
            YggdrasilPacket response = process(request);
            sendData(response, request.address, request.port);
        }
        core.logInfo("YggdrasilAgentServer: finished");
    }

    private byte[] stringToByteArray(String string) {
        int len = string.length();
        byte[] resultArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            resultArray[i / 2] = (byte) ((Character.digit(string.charAt(i), 16) << 4)
                    + Character.digit(string.charAt(i+1), 16));
        }
        return resultArray;
    }

    private String byteArrayToString(byte[] byteArray) {
        String HEXES = "0123456789ABCDEF";
        String resultString = "";
        for (byte singleByte: byteArray) {
            resultString += HEXES.charAt((singleByte & 0xF0) >> 4);
            resultString += HEXES.charAt((singleByte & 0x0F));
        }
        return resultString;
    }

    private YggdrasilPacket process(YggdrasilPacket request) {
        YggdrasilPacket response = new YggdrasilPacket(request.opCode, request.subOpCode, request.token, null);
        if (request.opCode == (byte)0x00) {
            if (Arrays.equals(request.token, new byte[8])) {
                core.logInfo("YggdrasilAgentServer: New agent found:");
                core.logInfo("\tIP: " + request.address.getHostAddress());
            }
        }
        return response;
    }

    @Override
    protected void onInit() {
        core.logInfo("YggdrasilAgentServer: Initializing YggdrasilAgentServer...");

        try {
            this.serverHost = InetAddress.getByName(core.getConfig("YggdrasilAgentHost", "0.0.0.0"));
        } catch (UnknownHostException e) {
            core.logWarning("Unable to parse AgentServer host. Setting to loopback address: " + e.getMessage());
            this.serverHost = InetAddress.getLoopbackAddress();
        }
        this.serverPort = Integer.parseInt(core.getConfig("YggdrasilAgentPort", "1488"));

        try {
            this.serverSocket = new DatagramSocket(this.serverPort, this.serverHost);
            this.serverSocket.setBroadcast(true);
            core.logInfo("YggdrasilAgentServer: Start listening on " + this.serverHost.getHostAddress() + ":" + this.serverPort);
        } catch (SocketException e) {
            core.logWarning("Unable to create UDP socket: " + e.getMessage());
        }

        this.service = new YggdrasilUDPService(core);
    }

    @Override
    protected void onShutdown() {

    }
}
