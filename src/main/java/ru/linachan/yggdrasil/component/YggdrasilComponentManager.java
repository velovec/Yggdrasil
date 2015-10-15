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
        if (Boolean.valueOf(core.getConfig(component.getName().replace("Core", "Enabled"), "false"))) {
            components.put(component.getName(), component);
            component.initializeComponent(core);
        } else {
            core.logInfo("YggdrasilComponentManager: Skipping " + component.getName() + ": disabled");
        }
    }

    public YggdrasilComponent getComponent(String componentName) {
        if (components.containsKey(componentName)) {
            return components.get(componentName);
        }
        return null;
    }

    public boolean componentRegistered(String componentName) {
        return components.containsKey(componentName);
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
