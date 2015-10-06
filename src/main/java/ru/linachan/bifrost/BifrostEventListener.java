package ru.linachan.bifrost;

import org.firmata4j.IODeviceEventListener;
import org.firmata4j.IOEvent;
import ru.linachan.yggdrasil.YggdrasilCore;

public class BifrostEventListener implements IODeviceEventListener {

    private YggdrasilCore core;

    public BifrostEventListener(YggdrasilCore yggdrasilCore) {
        this.core = yggdrasilCore;
    }

    @Override
    public void onStart(IOEvent ioEvent) {
        core.logInfo("BifrostEventListener: Connection established");
    }

    @Override
    public void onStop(IOEvent ioEvent) {
        core.logInfo("BifrostEventListener: Disconnected");
    }

    @Override
    public void onPinChange(IOEvent ioEvent) {

    }

    @Override
    public void onMessageReceive(IOEvent ioEvent, String s) {

    }
}
