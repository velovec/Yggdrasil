package ru.linachan.yggdrasil;

import ru.linachan.alfheim.AlfheimCore;
import ru.linachan.asgard.AsgardCore;
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
import ru.linachan.yggdrasil.service.YggdrasilService;
import ru.linachan.yggdrasil.service.YggdrasilServiceRunner;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class YggdrasilCore {

    private AsgardCore db;
    private NiflheimCore security;
    private ValkyrieCore broker;
    private FenrirCore auth;
    private JormungandCore executor;
    private UrdCore cache;
    private AlfheimCore builder;
    private LokiCore browser;
    private BifrostCore bridge;
    private ValhallaCore scheduler;
    private SkuldCore voice;
    private MidgardCore api;

    private YggdrasilShutdownHook shutdownHook;
    private YggdrasilServiceRunner serviceRunner;

    private Properties cfg;

    static {
        LogManager.getLogManager().reset();
    }

    private Logger logger = Logger.getLogger("Yggdrasil");

    private PrintStream fakeOutput;
    private PrintStream originalOutput;

    private boolean runningYggdrasil = true;

    public YggdrasilCore(String cfg) {

        this.configLogger();

        this.cfg = new Properties();
        try {
            this.cfg.load(new FileInputStream(cfg));
        } catch(IOException e) {
            this.logWarning("Config Error: " + e.getMessage());
        }

        this.shutdownHook = new YggdrasilShutdownHook(this);
        this.serviceRunner = new YggdrasilServiceRunner(this);

        this.registerShutdownHook();

        this.scheduler  = new ValhallaCore(this);   // Instantiate scheduler
        this.executor   = new JormungandCore(this); // Instantiate executor system
        this.cache      = new UrdCore(this);        // Instantiate cache system
        this.db         = new AsgardCore(this);     // Instantiate main data storage system
        this.broker     = new ValkyrieCore(this);   // Instantiate main message transport system
        this.auth       = new FenrirCore(this);     // Instantiate main authorization system
        this.security   = new NiflheimCore(this);   // Instantiate main security system
        this.builder    = new AlfheimCore(this);    // Instantiate image builder
        this.browser    = new LokiCore(this);       // Instantiate web browser driver
        this.bridge     = new BifrostCore(this);    // Instantiate peripheral bridge
        this.voice      = new SkuldCore(this);      // Instantiate real-time voice processor
        this.api        = new MidgardCore(this);    // Instantiate API server

        this.originalOutput = System.out;
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this.shutdownHook));
    }

    private void configLogger() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new YggdrasilLogFormatter());

        for (Handler logHandler : this.logger.getHandlers()) {
            this.logger.removeHandler(logHandler);
        }
        this.logger.setUseParentHandlers(false);
        this.logger.addHandler(handler);
    }

    public String getConfig(String propertyName, String defaultValue) {
        return this.cfg.getProperty(propertyName, defaultValue);
    }

    public void logInfo(String message) {
        this.logger.info(message);
    }

    public void logWarning(String message) {
        this.logger.warning(message);
    }

    public void logException(Exception exc) {
        StackTraceElement[] stack_trace = exc.getStackTrace();
        this.logger.severe("##### " + exc.getClass().getSimpleName() + " :: " + exc.getMessage() + " #####");
        for (StackTraceElement line : stack_trace) {
            this.logger.severe("File " + line.getFileName() + " in method " + line.getMethodName() + " at line " + line.getLineNumber());
        }
        this.logger.severe("##### END OF TRACE #####");
    }

    public void mainLoop() throws InterruptedException, IOException {
        if (executeTests()) {
            startService(new YggdrasilAgentServer());

            while (this.runningYggdrasil) {
                Thread.sleep(1000);
            }
            this.logInfo("Yggdrasil main loop finished. Waiting another services to finish...");

            serviceRunner.shutdownServices();
        } else {
            shutdownYggdrasil();
        }

        executor.shutdownJormungand();
        bridge.shutdownBifrost();
        scheduler.shutdownValhalla();

        this.logInfo("Yggdrasil is down...");
    }

    public void startService(YggdrasilService service) {
        this.serviceRunner.startService(service);
    }

    public void stopService(String serviceName, Boolean wait) {
        this.serviceRunner.stopService(serviceName, wait);
    }

    public void enableFakeOutput() {
        this.fakeOutput = new PrintStream(new ByteArrayOutputStream());
        System.setOut(this.fakeOutput);
    }

    public void disableFakeOutput() {
        System.setOut(this.originalOutput);
        this.fakeOutput = null;
    }

    public boolean executeTests() {
        logInfo("YggdrasilCore: Initializing self-diagnostic");

        boolean dbOk = this.db.executeTests();
        logInfo("AsgardCore: " + ((dbOk) ? "PASS" : "FAIL"));

        boolean securityOk = this.security.executeTests();
        logInfo("NiflheimCore: " + ((securityOk) ? "PASS" : "FAIL"));

        boolean brokerOk = this.broker.executeTests();
        logInfo("ValkyrieCore: " + ((brokerOk) ? "PASS" : "FAIL"));

        boolean authOk = this.auth.executeTests();
        logInfo("FenrirCore: " + ((authOk) ? "PASS" : "FAIL"));

        boolean executorOk = this.executor.execute_tests();
        logInfo("JormungandCore: " + ((executorOk) ? "PASS" : "FAIL"));

        boolean cacheOk = this.cache.execute_tests();
        logInfo("UrdCore: " + ((cacheOk) ? "PASS" : "FAIL"));

        boolean builderOk = this.builder.execute_tests();
        logInfo("AlfheimCore: " + ((builderOk) ? "PASS" : "FAIL"));

        boolean browserOk = this.browser.execute_tests();
        logInfo("LokiCore: " + ((browserOk) ? "PASS" : "FAIL"));

        boolean bridgeOk = this.bridge.execute_tests();
        logInfo("BifrostCore: " + ((bridgeOk) ? "PASS" : "FAIL"));

        boolean schedulerOk = this.scheduler.execute_tests();
        logInfo("ValhallaCore: " + ((schedulerOk) ? "PASS" : "FAIL"));

        boolean voiceOk = this.voice.execute_tests();
        logInfo("SkuldCore: " + ((voiceOk) ? "PASS" : "FAIL"));

        boolean apiOk = this.api.execute_tests();
        logInfo("MidgardCore: " + ((apiOk) ? "PASS" : "FAIL"));

        if (dbOk && securityOk && brokerOk && authOk && executorOk && cacheOk && builderOk && browserOk && bridgeOk && schedulerOk && voiceOk && apiOk) {
            logInfo("YggdrasilCore: Self-diagnostic successfully completed");
            return true;
        } else {
            logWarning("YggdrasilCore: Self-diagnostic failed");
            return false;
        }
    }

    public void shutdownYggdrasil() {
        this.runningYggdrasil = false;
        this.logInfo("Shutting down Yggdrasil...");
    }

    public boolean isRunningYggdrasil() {
        return runningYggdrasil;
    }

    public AsgardCore getDBManager() {
        return db;
    }

    public FenrirCore getAuthManager() {
        return auth;
    }

    public NiflheimCore getSecurityManager() {
        return security;
    }

    public ValkyrieCore getBroker() {
        return broker;
    }

    public JormungandCore getExecutionManager() {
        return executor;
    }

    public UrdCore getCacheManager() {
        return cache;
    }

    public AlfheimCore getImageBuilder() { return builder; }

    public LokiCore getBrowser() {
        return browser;
    }

    public BifrostCore getBridge() { return bridge; }

    public ValhallaCore getScheduler() { return scheduler; }
}
