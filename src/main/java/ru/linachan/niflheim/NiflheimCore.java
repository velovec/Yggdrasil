package ru.linachan.niflheim;

import ru.linachan.yggdrasil.YggdrasilCore;

public class NiflheimCore {

    private YggdrasilCore core;

    public NiflheimCore(YggdrasilCore yggdrasilCore) {
        this.core = yggdrasilCore;

        this.core.logInfo("Initializing Niflheim Security System...");
    }

    public boolean executeTests() {
        return true;
    }
}
