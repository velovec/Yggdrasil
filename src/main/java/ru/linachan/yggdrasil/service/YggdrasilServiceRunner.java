package ru.linachan.yggdrasil.service;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.util.HashMap;
import java.util.Map;

public class YggdrasilServiceRunner {

    private YggdrasilCore core;
    private Map<String, YggdrasilService> serviceMap = new HashMap<>();

    public YggdrasilServiceRunner(YggdrasilCore core) {
        this.core = core;
    }

    public boolean startService(YggdrasilService service) {
        return startService(service, service.getClass().getSimpleName());
    }

    public boolean startService(YggdrasilService service, String serviceName) {
        if (!serviceMap.containsKey(serviceName)) {
            service.initializeService(this.core, serviceName);
            Thread serviceThread = new Thread(service);
            service.setServiceThread(serviceThread);

            serviceMap.put(serviceName, service);

            serviceThread.start();

            return true;
        } else {
            this.core.logWarning("Service '" + serviceName + "' already exists!");
            return false;
        }
    }

    public boolean stopService(String serviceName, Boolean wait) {
        if (serviceMap.containsKey(serviceName)) {
            YggdrasilService service = serviceMap.get(serviceName);
            try {
                service.stop(wait);
            } catch (InterruptedException e) {
                this.core.logException(e);
            }
            return true;
        } else {
            return false;
        }
    }

    public void shutdownServices() {
        for (String serviceName: serviceMap.keySet()) {
            stopService(serviceName, false);
        }
    }
}
