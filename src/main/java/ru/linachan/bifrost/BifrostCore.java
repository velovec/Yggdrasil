package ru.linachan.bifrost;

import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ui.JPinboard;
import ru.linachan.yggdrasil.component.YggdrasilComponent;

import javax.swing.*;
import java.io.IOException;
import java.util.Set;

public class BifrostCore extends YggdrasilComponent {

    private IODevice firmataDevice;
    private BifrostEventListener eventListener;

    private boolean exitOnFailure;
    private boolean runControlPanel;

    private boolean deviceIsReady;

    @Override
    protected void onInit() {
        String peripheralPortName = core.getConfig("BifrostPort", "/dev/ttyACM0");
        exitOnFailure = Boolean.valueOf(core.getConfig("BifrostExitOnFailure", "false"));
        runControlPanel = Boolean.valueOf(core.getConfig("BifrostRunControlPanel", "false"));

        firmataDevice = new FirmataDevice(peripheralPortName);
        eventListener = new BifrostEventListener(core);

        try {
            firmataDevice.addEventListener(eventListener);
            firmataDevice.start();
            firmataDevice.ensureInitializationIsDone();

            deviceIsReady = true;

            if (runControlPanel) {
                runControlPanel();
            }
        } catch (InterruptedException | IOException e) {
            if (e.getMessage().equals("Cannot start firmata device")) {
                deviceIsReady = false;
            } else {
                core.logException(e);
            }
        }

        core.logInfo("BifrostCore: Firmata device at '" + peripheralPortName + "' " + ((deviceIsReady) ? "is ready" : "is not ready"));
    }

    @Override
    protected void onShutdown() {
        if (deviceIsReady&&firmataDevice.isReady()) {
            try {
                firmataDevice.stop();
            } catch (IOException e) {
                core.logException(e);
            }
        }
    }

    @Override
    public boolean executeTests() {
        return !exitOnFailure || firmataDevice.isReady();
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
}
