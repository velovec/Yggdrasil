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
        components.put(component.getName(), component);
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

        core.logInfo("YggdrasilCore: Initializing self-diagnostic");

        for (String componentName: components.keySet()) {
            boolean componentResult = components.get(componentName).executeTests();

            core.logInfo(componentName + ((componentResult) ? "PASS" : "FAIL"));

            testsResult = testsResult && componentResult;
        }

        if (testsResult) {
            core.logInfo("YggdrasilCore: Self-diagnostic successfully completed");
        } else {
            core.logWarning("YggdrasilCore: Self-diagnostic failed");
        }

        return testsResult;
    }

    public void shutdown() {

    }
}
