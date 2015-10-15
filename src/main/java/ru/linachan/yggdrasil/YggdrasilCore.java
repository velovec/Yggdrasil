package ru.linachan.yggdrasil;

import ru.linachan.alfheim.AlfheimCore;
import ru.linachan.bifrost.BifrostCore;
import ru.linachan.fenrir.FenrirCore;
import ru.linachan.jormungand.JormungandCore;
import ru.linachan.loki.LokiCore;
import ru.linachan.midgard.MidgardCore;
import ru.linachan.niflheim.NiflheimCore;
import ru.linachan.skuld.SkuldCore;
import ru.linachan.urd.UrdCore;
import ru.linachan.valhalla.ValhallaCore;
import ru.linachan.valkyrie.ValkyrieCore;
import ru.linachan.yggdrasil.component.YggdrasilComponent;
import ru.linachan.yggdrasil.component.YggdrasilComponentManager;
import ru.linachan.yggdrasil.service.YggdrasilService;
import ru.linachan.yggdrasil.service.YggdrasilServiceRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class YggdrasilCore {

    private YggdrasilShutdownHook shutdownHook;
    private YggdrasilServiceRunner serviceRunner;
    private YggdrasilComponentManager componentManager;

    private Properties cfg;

    static {
        LogManager.getLogManager().reset();
    }

    private Logger logger = Logger.getLogger("Yggdrasil");

    private boolean runningYggdrasil = true;

    public YggdrasilCore(String configFile) {

        configLogger();

        cfg = new Properties();
        try {
            cfg.load(new FileInputStream(configFile));
        } catch(IOException e) {
            logWarning("Config Error: " + e.getMessage());
        }

        shutdownHook = new YggdrasilShutdownHook(this);
        serviceRunner = new YggdrasilServiceRunner(this);
        componentManager = new YggdrasilComponentManager(this);

        registerShutdownHook();

        componentManager.registerComponent(new ValhallaCore());   // Instantiate scheduler
        componentManager.registerComponent(new JormungandCore()); // Instantiate executor system
        componentManager.registerComponent(new UrdCore());        // Instantiate cache system
        componentManager.registerComponent(new ValkyrieCore());   // Instantiate main message transport system
        componentManager.registerComponent(new FenrirCore());     // Instantiate main authorization system
        componentManager.registerComponent(new NiflheimCore());   // Instantiate main security system
        componentManager.registerComponent(new AlfheimCore());    // Instantiate hypervisor manager
        componentManager.registerComponent(new LokiCore());       // Instantiate web browser driver
        componentManager.registerComponent(new BifrostCore());    // Instantiate peripheral bridge
        componentManager.registerComponent(new SkuldCore());      // Instantiate real-time audio processor
        componentManager.registerComponent(new MidgardCore());    // Instantiate API server

    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
    }

    private void configLogger() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new YggdrasilLogFormatter());

        for (Handler logHandler : logger.getHandlers()) {
            logger.removeHandler(logHandler);
        }
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
    }

    public String getConfig(String propertyName, String defaultValue) {
        return cfg.getProperty(propertyName, defaultValue);
    }

    public void logInfo(String message) {
        logger.info(message);
    }

    public void logWarning(String message) {
        logger.warning(message);
    }

    public void logException(Exception exc) {
        StackTraceElement[] stack_trace = exc.getStackTrace();
        logger.severe("##### " + exc.getClass().getSimpleName() + " :: " + exc.getMessage() + " #####");
        for (StackTraceElement line : stack_trace) {
            logger.severe("File " + line.getFileName() + " in method " + line.getMethodName() + " at line " + line.getLineNumber());
        }
        logger.severe("##### END OF TRACE #####");
    }

    public void mainLoop() throws InterruptedException, IOException {
        if (componentManager.executeTests()) {
            startService(new YggdrasilAgentServer());

            while (runningYggdrasil) {
                Thread.sleep(1000);
            }

            logInfo("Yggdrasil main loop finished. Waiting another services to finish...");

        } else {
            shutdownYggdrasil();
        }

        serviceRunner.shutdown();
        componentManager.shutdown();

        logInfo("Yggdrasil is down...");
    }

    public void startService(YggdrasilService service) {
        serviceRunner.startService(service);
    }

    public void stopService(String serviceName, Boolean wait) {
        serviceRunner.stopService(serviceName, wait);
    }

    public void shutdownYggdrasil() {
        runningYggdrasil = false;
        logInfo("Shutting down Yggdrasil...");
    }

    public boolean isRunningYggdrasil() {
        return runningYggdrasil;
    }

    public <T extends YggdrasilComponent> T getComponent(Class<T> component) {
        if (componentManager.componentRegistered(component.getSimpleName())) {
            return component.cast(componentManager.getComponent(component.getSimpleName()));
        }
        return null;
    }
}
