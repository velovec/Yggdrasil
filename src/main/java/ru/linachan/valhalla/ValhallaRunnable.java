package ru.linachan.valhalla;

import ru.linachan.yggdrasil.YggdrasilCore;

public abstract class ValhallaRunnable implements Runnable {

    protected YggdrasilCore core;

    public ValhallaRunnable(YggdrasilCore core) {
        this.core = core;
    }

    public abstract void run();

    public abstract void onCancel();
}
