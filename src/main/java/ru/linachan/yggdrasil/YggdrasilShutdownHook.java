package ru.linachan.yggdrasil;

public class YggdrasilShutdownHook implements Runnable {

    private YggdrasilCore core;

    public YggdrasilShutdownHook(YggdrasilCore core) {
        this.core = core;
    }

    @Override
    public void run() {
        this.core.shutdownYggdrasil();
    }
}
