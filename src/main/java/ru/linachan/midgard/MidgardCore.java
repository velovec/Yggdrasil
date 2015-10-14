package ru.linachan.midgard;

import ru.linachan.yggdrasil.YggdrasilCore;

public class MidgardCore {

    private YggdrasilCore core;
    private MidgardServer server;

    public MidgardCore(YggdrasilCore core) {
        this.core = core;
        this.server = new MidgardServer();

        this.core.startService(this.server);
    }

    public boolean execute_tests() {
        return this.server.isRunning();
    }
}
