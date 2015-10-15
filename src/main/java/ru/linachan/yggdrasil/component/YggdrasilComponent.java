package ru.linachan.yggdrasil.component;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.util.ArrayList;
import java.util.List;

public abstract class YggdrasilComponent {

    protected YggdrasilCore core;
    protected static List<Class<? extends YggdrasilComponent>> dependencies = new ArrayList<>();

    public void initializeComponent(YggdrasilCore core) {
        this.core = core;

        checkDependencies();
        onInit();

        this.core.logInfo(this.getClass().getSimpleName() + " initialized");
    }

    protected static <T extends YggdrasilComponent> void dependsOn(Class<T> dependency) {
        if (!dependencies.contains(dependency)) {
            dependencies.add(dependency);
        }
    }

    private void checkDependencies() {
        for (Class<? extends YggdrasilComponent> dependency : dependencies) {
            if (!core.componentEnabled(dependency)) {
                core.enableComponent(dependency, true);
            }
        }
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
