package ru.linachan.yggdrasil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class YggdrasilUDPService {
    
    private YggdrasilCore core;
    
    public YggdrasilUDPService(YggdrasilCore core) {
        this.core = core;
    }

    private Integer calculateMapSize(Map<String, String> map) {
        Integer totalSize = 0;
        if (map != null) {
            for (String key : map.keySet()) {
                totalSize += 8 + key.getBytes().length + map.get(key).getBytes().length;
            }
        }
        return totalSize;
    }

    private byte[] packMapToBytes(Map<String, String> map) {
        if (map != null) {
            ByteBuffer mapBuffer = ByteBuffer.allocate(calculateMapSize(map));

            for (String key: map.keySet()) {
                byte[] keyArray = key.getBytes();
                byte[] valueArray = map.get(key).getBytes();

                mapBuffer.putInt(keyArray.length);
                mapBuffer.putInt(valueArray.length);
                mapBuffer.put(keyArray);
                mapBuffer.put(valueArray);
            }

            return mapBuffer.array();
        } else {
            return new byte[0];
        }
    }

    private Map<String, String> unpackMapFromBytes(ByteBuffer inputDataBuffer, Integer dataSize) {
        Map<String, String> map = new HashMap<>();
        Integer unpackedSize = 0;
        while (true) {
            try {
                Integer keyLength = inputDataBuffer.getInt();
                Integer valueLength = inputDataBuffer.getInt();

                byte[] keyArray = new byte[keyLength];
                byte[] valueArray = new byte[valueLength];

                inputDataBuffer.get(keyArray);
                inputDataBuffer.get(valueArray);

                map.put(new String(keyArray), new String(valueArray));
                unpackedSize += 8 + keyLength + valueLength;

                if (unpackedSize >= dataSize) {
                    break;
                }
            } catch (BufferUnderflowException e) {
                break;
            }
        }
        return map;
    }

    public boolean sendData(DatagramSocket socket, YggdrasilPacket packet, InetAddress address, Integer port) {
        try {
            ByteBuffer outputBuffer = ByteBuffer.allocate(14 + calculateMapSize(packet.parameters));
            outputBuffer.put(packet.opCode);
            outputBuffer.put(packet.subOpCode);
            outputBuffer.put(packet.token);
            outputBuffer.putInt(calculateMapSize(packet.parameters));
            outputBuffer.put(packMapToBytes(packet.parameters));

            DatagramPacket outputPacket = new DatagramPacket(outputBuffer.array(), outputBuffer.array().length, address, port);

            socket.send(outputPacket);
            return true;
        } catch (IOException e) {
            core.logWarning("Unable to send UDP packet: " + e.getMessage());
        }
        return false;
    }

    public YggdrasilPacket recvData(DatagramSocket socket) {
        try {
            byte[] receiveBuffer = new byte[65536];
            DatagramPacket response = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(response);

            ByteBuffer inputBuffer = ByteBuffer.wrap(receiveBuffer);
            Byte opCode = inputBuffer.get();
            Byte subOpCode = inputBuffer.get();
            byte[] token = new byte[8];
            inputBuffer.get(token);
            Integer dataSize = inputBuffer.getInt();

            return new YggdrasilPacket(opCode, subOpCode, token, unpackMapFromBytes(inputBuffer, dataSize), response.getAddress(), response.getPort());

        } catch (IOException e) {
            core.logWarning("Unable to receive UDP packet:" + e.getMessage());
        }
        return null;
    }
}
