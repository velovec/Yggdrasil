package ru.linachan.yggdrasil;

import org.jooq.DSLContext;
import ru.linachan.asgard.orm.Tables;
import ru.linachan.yggdrasil.service.YggdrasilService;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

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

    private Integer countAgents() {
        DSLContext context = core.getDBManager().getContext();
        return context.select().from(Tables.AGENT_DATA).fetchCount();
    }

    private byte[] registerAgent(String osName, String osVer, Long totalMem, String cpuName, Integer cpuCores) {
        byte[] accessToken = new byte[8];
        new Random().nextBytes(accessToken);

        DSLContext context = core.getDBManager().getContext();
        context.insertInto(
            Tables.AGENT_DATA,
            Tables.AGENT_DATA.OS_NAME,
            Tables.AGENT_DATA.OS_VERSION,
            Tables.AGENT_DATA.TOTAL_RAM,
            Tables.AGENT_DATA.CPU_NAME,
            Tables.AGENT_DATA.CPU_CORES,
            Tables.AGENT_DATA.ACCESS_TOKEN
        ).values(
                osName, osVer, totalMem, cpuName, cpuCores, byteArrayToString(accessToken)
        ).execute();

        return accessToken;
    }

    private YggdrasilPacket process(YggdrasilPacket request) {
        YggdrasilPacket response = new YggdrasilPacket(request.opCode, request.subOpCode, request.token, null);
        if (request.opCode == (byte)0x00) {
            if (Arrays.equals(request.token, new byte[8])) {
                core.logInfo("YggdrasilAgentServer: New agent found:");
                core.logInfo("\tIP: " + request.address.getHostAddress());
                response.token = registerAgent(
                    request.parameters.get("OS_NAME"),
                    request.parameters.get("OS_VERSION"),
                    Long.parseLong(request.parameters.get("TOTAL_MEM")),
                    request.parameters.get("CPU_NAME"),
                    Integer.parseInt(request.parameters.get("CPU_CORES"))
                );
            }
        }
        return response;
    }

    @Override
    protected void onInit() {
        core.logInfo("YggdrasilAgentServer: Initializing YggdrasilAgentServer...");
        core.logInfo("YggdrasilAgentServer: " + String.valueOf(countAgents()) + " agent(s) registered");

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
