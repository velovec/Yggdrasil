package ru.linachan.yggdrasil.service;

import ru.linachan.yggdrasil.YggdrasilCore;

public abstract class YggdrasilService implements Runnable {

    protected YggdrasilCore core;
    private String serviceName;
    private Thread serviceThread;
    protected Boolean isRunning;

    public void initializeService(YggdrasilCore core, String serviceName) {
        this.core = core;
        this.serviceName = serviceName;

        this.core.logInfo("YggdrasilService: Service '" + serviceName + "' initialized");

        this.isRunning = true;

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
        this.core.logInfo("YggdrasilService: Service '" + serviceName + "' stopped");

        onShutdown();

        this.isRunning = false;

        if (wait) {
            while (serviceThread.isAlive()) {
                Thread.sleep(1000);
            }
            this.serviceThread.join();
        }

        this.serviceThread.interrupt();
    }

    public boolean isRunning() {
        return isRunning;
    }
}
