package ru.linachan.yggdrasil.component;

import ru.linachan.yggdrasil.YggdrasilCore;

public abstract class YggdrasilComponent {

    protected YggdrasilCore core;
    private String componentName;

    public void initializeComponent(YggdrasilCore core) {
        this.core = core;

        onInit();

        this.componentName = this.getClass().getSimpleName();
        this.core.logInfo(componentName + " initialized");
    }

    protected abstract void onInit();

    protected abstract void onShutdown();

    public abstract boolean executeTests();

    public void shutdown() {
        this.core.logWarning(componentName + " is ready for shutdown");
    }

    public String getName() {
        return componentName;
    }
}
