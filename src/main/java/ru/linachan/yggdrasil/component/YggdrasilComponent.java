package ru.linachan.yggdrasil.component;

import ru.linachan.yggdrasil.YggdrasilCore;

public abstract class YggdrasilComponent {

    protected YggdrasilCore core;

    public void initializeComponent(YggdrasilCore core) {
        this.core = core;

        onInit();

        this.core.logInfo(this.getClass().getSimpleName() + " initialized");
    }

    protected abstract void onInit();

    protected abstract void onShutdown();

    public abstract boolean executeTests();

    public void shutdown() {
        this.core.logWarning(this.getClass().getSimpleName() + " is ready for shutdown");
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }
}
