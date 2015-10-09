package ru.linachan.yggdrasil.service;

import ru.linachan.yggdrasil.YggdrasilCore;

public abstract class YggdrasilService implements Runnable {

    protected YggdrasilCore core;
    private String serviceName;
    private Thread serviceThread;

    public void initializeService(YggdrasilCore core, String serviceName) {
        this.core = core;
        this.serviceName = serviceName;

        this.core.logInfo("Initializing service '" + serviceName + "'...");

        onInit();
    }

    public void setServiceThread(Thread serviceThread) {
        this.serviceThread = serviceThread;
    }

    protected abstract void onInit();

    protected abstract void onShutdown();

    public void stop() throws InterruptedException {
        stop(false);
    }

    public void stop(Boolean wait) throws InterruptedException {
        this.core.logInfo("Shutting service '" + serviceName + "' down...");

        onShutdown();

        if (wait) {
            while (serviceThread.isAlive()) {
                Thread.sleep(1000);
            }
            this.serviceThread.join();
        }

        this.serviceThread.interrupt();
    }
}
