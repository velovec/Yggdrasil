package ru.linachan.valhalla;

import ru.linachan.yggdrasil.YggdrasilCore;

public abstract class ValhallaRunnable implements Runnable {

    protected YggdrasilCore core;

    public ValhallaRunnable(YggdrasilCore core) {
        this.core = core;
    }

    public abstract void run();

    public abstract void onCancel();

    protected void logInfo(String message) {
        this.core.logInfo("ValhallaTask: " + message);
    }

    protected void logWarning(String message) {
        this.core.logWarning("ValhallaTask: " + message);
    }

    protected void logException(Exception e) {
        this.core.logException(e);
    }
}
