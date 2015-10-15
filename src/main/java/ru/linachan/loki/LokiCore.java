package ru.linachan.loki;

import ru.linachan.yggdrasil.component.YggdrasilComponent;

public class LokiCore extends YggdrasilComponent {

    @Override
    protected void onInit() {

    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean executeTests() {
        return true;
    }

    public LokiDriver getDriver() {
        return new LokiDriver(this.core, this);
    }
}
