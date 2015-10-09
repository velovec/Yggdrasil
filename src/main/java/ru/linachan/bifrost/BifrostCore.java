package ru.linachan.bifrost;

import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ui.JPinboard;
import ru.linachan.yggdrasil.YggdrasilCore;

import javax.swing.*;
import java.io.IOException;
import java.util.Set;

public class BifrostCore {

    private YggdrasilCore core;

    private IODevice firmataDevice;
    private BifrostEventListener eventListener;

    private boolean exitOnFailure;
    private boolean runControlPanel;

    private boolean deviceIsReady;

    public BifrostCore(YggdrasilCore core) {
        this.core = core;

        this.core.logInfo("Initializing Bifrost Peripheral Bridge...");

        String peripheralPortName = this.core.getConfig("BifrostPort", "/dev/ttyACM0");
        this.exitOnFailure = Boolean.valueOf(this.core.getConfig("BifrostExitOnFailure", "false"));
        this.runControlPanel = Boolean.valueOf(this.core.getConfig("BifrostRunControlPanel", "false"));

        this.firmataDevice = new FirmataDevice(peripheralPortName);
        this.eventListener = new BifrostEventListener(core);

        try {
            this.firmataDevice.addEventListener(eventListener);
            this.firmataDevice.start();
            this.firmataDevice.ensureInitializationIsDone();

            this.deviceIsReady = true;

            if (this.runControlPanel) {
                runControlPanel();
            }
        } catch (InterruptedException | IOException e) {
            if (e.getMessage().equals("Cannot start firmata device")) {
                this.deviceIsReady = false;
            } else {
                core.logException(e);
            }
        }

        this.core.logInfo("BifrostCore: Firmata device at '" + peripheralPortName + "' " + ((deviceIsReady) ? "is ready" : "is not ready"));
    }

    public void runControlPanel() {
        JPinboard pinboard = new JPinboard(firmataDevice);
        JFrame frame = new JFrame("Bifrost Control Panel");
        frame.add(pinboard);
        frame.pack();
        frame.setVisible(true);
    }

    public Pin getPin(int pin) {
        return firmataDevice.getPin(pin);
    }

    public Set<Pin> getPinList() {
        return firmataDevice.getPins();
    }

    public void shutdownBifrost() {
        if (deviceIsReady&&firmataDevice.isReady()) {
            try {
                firmataDevice.stop();
            } catch (IOException e) {
                core.logException(e);
            }
        }
    }

    public boolean execute_tests() {
        return !exitOnFailure || firmataDevice.isReady();
    }
}
