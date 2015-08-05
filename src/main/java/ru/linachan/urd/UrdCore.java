package ru.linachan.urd;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.File;
import java.nio.ByteBuffer;

public class UrdCore {

    private YggdrasilCore core;
    private boolean cacheLocked = false;
    private final Long MAGIC_HEADER = 0x55524476302e31L;
    private String cacheFilePath;
    private File cacheFile;

    public UrdCore(YggdrasilCore yggdrasilCore) {
        this.core = yggdrasilCore;

        this.cacheFilePath = this.core.getConfig("UrdStorageFile", "UrdStorage.urd");
        this.cacheFile = new File(this.cacheFilePath);

        if (!this.cacheFile.exists()) {
            initializeCacheStorage();
        }
    }

    private void waitAndLock() {
        while (cacheLocked) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                this.core.logException(e);
            }
        }
        cacheLocked = true;
    }

    private void unlock() {
        cacheLocked = false;
    }

    private void initializeCacheStorage() {
        waitAndLock();

        unlock();
    }

    public boolean execute_tests() {
        return true;
    }
}
