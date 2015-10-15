package ru.linachan.yggdrasil.component;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.util.HashMap;
import java.util.Map;

public class YggdrasilComponentManager {

    private YggdrasilCore core;
    private Map<String, YggdrasilComponent> components = new HashMap<>();

    public YggdrasilComponentManager(YggdrasilCore core) {
        this.core = core;
    }

    public void registerComponent(YggdrasilComponent component) {
        registerComponent(component, false);
    }

    public void registerComponent(YggdrasilComponent component, boolean registerIfDisabled) {
        boolean isComponentEnabled = Boolean.valueOf(core.getConfig(component.getName().replace("Core", "Enabled"), "false"));

        if (isComponentEnabled||registerIfDisabled) {
            components.put(component.getName(), component);
            component.initializeComponent(core);
        } else {
            core.logInfo("YggdrasilComponentManager: Skipping " + component.getName() + ": disabled");
        }
    }

    public <T extends YggdrasilComponent> void unRegisterComponent(Class<T> component) {
        if (componentRegistered(component)) {
            components.remove(component.getSimpleName());
        }
    }

    public <T extends YggdrasilComponent> YggdrasilComponent getComponent(Class<T> component) {
        if (components.containsKey(component.getSimpleName())) {
            return components.get(component.getSimpleName());
        }
        return null;
    }

    public <T extends YggdrasilComponent> boolean componentRegistered(Class<T> component) {
        return components.containsKey(component.getSimpleName());
    }

    public boolean executeTests() {
        boolean testsResult = true;

        core.logInfo("YggdrasilComponentManager: Initializing self-diagnostic");

        for (String componentName: components.keySet()) {
            boolean componentResult = components.get(componentName).executeTests();

            core.logInfo(componentName + ": " + ((componentResult) ? "PASS" : "FAIL"));

            testsResult = testsResult && componentResult;
        }

        if (testsResult) {
            core.logInfo("YggdrasilComponentManager: Self-diagnostic successfully completed");
        } else {
            core.logWarning("YggdrasilComponentManager: Self-diagnostic failed");
        }

        return testsResult;
    }

    public void shutdown() {
        for (String componentName: components.keySet()) {
            components.get(componentName).shutdown();
        }
    }
}
