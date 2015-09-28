package ru.linachan.jormungand;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.util.concurrent.Semaphore;

public class JormungandCore {

    private YggdrasilCore core;
    private Semaphore executorLock = new Semaphore(1);

    public JormungandCore(YggdrasilCore yggdrasilCore) {
        this.core = yggdrasilCore;
    }

    public void acquire_lock() throws InterruptedException {
        executorLock.acquire();
    }

    public void release_lock() {
        executorLock.release();
    }

    public JormungandSubProcess getExecutor(String... cmd) {
        return new JormungandSubProcess(this.core, this, cmd);
    }

    public boolean execute_tests() {
        JormungandSubProcess test_exec = getExecutor("ls", "-la");

        Integer retVal = test_exec.run();

        return retVal == 0;
    }
}
