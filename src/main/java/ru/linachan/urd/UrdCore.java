package ru.linachan.urd;

import ru.linachan.yggdrasil.component.YggdrasilComponent;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class UrdCore extends YggdrasilComponent {

    private final byte[] MAGIC_HEADER = "URDv0.1".getBytes();
    private String cacheFilePath;
    private File cacheFile;
    private Semaphore cacheLock;
    private Map<String, String> cacheMap = new HashMap<>();

    @Override
    protected void onInit() {
        cacheFilePath = core.getConfig("UrdCacheFile", "UrdStorage.urd");
        cacheFile = new File(cacheFilePath);

        cacheLock = new Semaphore(1);

        if (!cacheFile.exists()) {
            initializeCacheStorage();
        }

        readCacheFile();
    }

    @Override
    protected void onShutdown() {
        writeCacheFile();
    }

    @Override
    public boolean executeTests() {
        putStorageValue("test_key", "test_value");
        putBinaryStorageValue("test_binary_key", stringToByteArray("FFE0"));

        String test_value = getStorageValue("test_key", null);
        byte[] test_binary_value = getBinaryStorageValue("test_binary_key", new byte[0]);

        Boolean res = test_value.equals("test_value")&&Arrays.equals(test_binary_value, stringToByteArray("FFE0"));

        if (!res) {
            core.logWarning("Data mismatch:");
            core.logWarning("'" + test_value + "' should be 'test_value'");
            core.logWarning("'" + byteArrayToString(test_binary_value) + "' should be 'FFE0'");
        }

        res = res && unsetStorageValue("test_key");
        res = res && unsetStorageValue("test_binary_key");

        if (!res) {
            for (String key : cacheMap.keySet()) {
                core.logWarning(key + " :: " + cacheMap.get(key));
            }
        }

        return res;
    }

    private boolean lock() {
        try {
            cacheLock.acquire();
            return true;
        } catch (InterruptedException e) {
            core.logException(e);
        }

        return false;
    }

    private void unlock() {
        cacheLock.release();
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

    private void checkEqual(int source, int target) {
        if (source != target)
            core.logWarning("UrdCore: " + String.valueOf(source) + " bytes read instead of " + String.valueOf(target));
    }

    private void readCacheFile() {
        if (cacheFile.exists() && cacheFile.isFile()) {
            if (lock()) {
                try {
                    FileInputStream storageStream = new FileInputStream(cacheFile);
                    byte[] RAW_MAGIC_HEADER = new byte[MAGIC_HEADER.length];
                    checkEqual(storageStream.read(RAW_MAGIC_HEADER), RAW_MAGIC_HEADER.length);

                    if (Arrays.equals(RAW_MAGIC_HEADER, MAGIC_HEADER)) {
                        byte[] storageLengthByte = new byte[4];
                        checkEqual(storageStream.read(storageLengthByte), 4);
                        Integer storageLength = ByteBuffer.wrap(storageLengthByte).getInt();

                        byte[] storageArray = new byte[storageLength];
                        checkEqual(storageStream.read(storageArray), storageLength);
                        ByteBuffer storageBuffer = ByteBuffer.wrap(storageArray);

                        Integer storageBytesRead = 0;

                        while (storageBytesRead < storageLength) {
                            int keyLength = storageBuffer.getInt();
                            int valueLength = storageBuffer.getInt();

                            byte[] keyArray = new byte[keyLength];
                            byte[] valueArray = new byte[valueLength];

                            storageBuffer.get(keyArray);
                            storageBuffer.get(valueArray);

                            cacheMap.put(new String(keyArray), new String(valueArray));
                            storageBytesRead += 8 + keyLength + valueLength;
                        }
                    } else {
                        core.logWarning("UrdCore: Incorrect storage file");
                    }
                } catch (FileNotFoundException e) {
                    core.logWarning("UrdCore: Unable to locate storage file: " + e.getMessage());
                } catch (IOException e) {
                    core.logWarning("UrdCore: Unable to read storage file: " + e.getMessage());
                }
            }
            unlock();
        } else {
            core.logInfo("UrdCore: Initializing empty storage...");
            initializeCacheStorage();
        }
    }

    private void writeCacheFile() {
        if (cacheFile.exists() && cacheFile.isFile()) {
            if(lock()) {
                try {
                    FileOutputStream storageStream = new FileOutputStream(cacheFile);
                    storageStream.write(MAGIC_HEADER);

                    Integer storageSize = calculateMapSize(cacheMap);
                    ByteBuffer writeBuffer = ByteBuffer.allocate(4 + storageSize);
                    writeBuffer.putInt(storageSize);
                    for (String key : cacheMap.keySet()) {
                        byte[] keyArray = key.getBytes();
                        byte[] valueArray = cacheMap.get(key).getBytes();

                        writeBuffer.putInt(keyArray.length);
                        writeBuffer.putInt(valueArray.length);

                        writeBuffer.put(keyArray);
                        writeBuffer.put(valueArray);
                    }
                    storageStream.write(writeBuffer.array());

                    storageStream.close();
                } catch (FileNotFoundException e) {
                    core.logWarning("UrdCore: Unable to locate storage at: " + e.getMessage());
                } catch (IOException e) {
                    core.logWarning("UrdCore: Unable to write storage data to file: " + e.getMessage());
                }
            }
            unlock();
        } else {
            core.logWarning("UrdCore: Unable to write storage at: " + cacheFilePath);
        }
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

    public boolean hasStorageValue(String key) {
        return cacheMap.containsKey(key);
    }

    public String getStorageValue(String key, String defaultValue) {
        if (cacheMap.containsKey(key)) {
            return cacheMap.get(key);
        }
        return defaultValue;
    }

    public boolean putStorageValue(String key, String value) {
        if (!cacheMap.containsKey(key)) {
            cacheMap.put(key, value);
            writeCacheFile();
            return true;
        }
        return false;
    }

    public byte[] getBinaryStorageValue(String key, byte[] defaultValue) {
        if (cacheMap.containsKey(key)) {
            return stringToByteArray(cacheMap.get(key));
        }
        return defaultValue;
    }

    public boolean putBinaryStorageValue(String key, byte[] value) {
        if (!cacheMap.containsKey(key)) {
            cacheMap.put(key, byteArrayToString(value));
            writeCacheFile();
            return true;
        }
        return false;
    }

    public boolean unsetStorageValue(String key) {
        if (cacheMap.containsKey(key)) {
            cacheMap.remove(key);
            writeCacheFile();
            return !cacheMap.containsKey(key);
        }
        return false;
    }

    private void initializeCacheStorage() {
        if (lock()) {
            try {
                FileOutputStream storageStream = new FileOutputStream(cacheFile);
                storageStream.write(MAGIC_HEADER);
                storageStream.close();
            } catch (FileNotFoundException e) {
                core.logWarning("UrdCore: Unable to locate storage at: " + e.getMessage());
            } catch (IOException e) {
                core.logWarning("UrdCore: Unable to write storage data to file: " + e.getMessage());
            }

            unlock();
        }
    }
}
