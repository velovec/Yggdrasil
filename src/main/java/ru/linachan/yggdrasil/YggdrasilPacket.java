package ru.linachan.yggdrasil;

import java.net.InetAddress;
import java.util.Map;

public class YggdrasilPacket {

    public Byte opCode;
    public Byte subOpCode;
    public byte[] token;
    public Map<String, String> parameters;
    public InetAddress address;
    public Integer port;

    public YggdrasilPacket(Byte opCode, Byte subOpCode, byte[] token, Map<String, String> parameters, InetAddress address, Integer port) {
        this.opCode = opCode;
        this.subOpCode = subOpCode;
        this.token = token;
        this.parameters = parameters;
        this.address = address;
        this.port = port;
    }

    public YggdrasilPacket(Byte opCode, Byte subOpCode, byte[] token, Map<String, String> parameters) {
        this.opCode = opCode;
        this.subOpCode = subOpCode;
        this.token = token;
        this.parameters = parameters;
    }
}
