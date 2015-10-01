package ru.linachan.loki;

import ru.linachan.yggdrasil.YggdrasilCore;

public class LokiCore {

    private YggdrasilCore core;

    public LokiCore(YggdrasilCore core) {
        this.core = core;
    }

    public LokiDriver getDriver() {
        return new LokiDriver(this.core, this);
    }

    public boolean execute_tests() {
        return true;
    }
}
