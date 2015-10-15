package ru.linachan.midgard;

import ru.linachan.yggdrasil.component.YggdrasilComponent;

public class MidgardCore extends YggdrasilComponent {

    private MidgardServer server;

    @Override
    protected void onInit() {
        server = new MidgardServer();

        core.startService(server);
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean executeTests() {
        return server.isRunning();
    }
}
