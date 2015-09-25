package ru.linachan.jormungand;

import ru.linachan.yggdrasil.YggdrasilCore;

public class JormungandCore {

    private YggdrasilCore core;

    public JormungandCore(YggdrasilCore yggdrasilCore) {
        this.core = yggdrasilCore;

    }

    public boolean execute_tests() {
        return true;
    }
}
