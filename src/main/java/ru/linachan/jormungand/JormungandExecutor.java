package ru.linachan.jormungand;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class JormungandExecutor implements Runnable {

    private YggdrasilCore yggdrasilCore;
    private JormungandCore jormungandCore;

    private Queue<Long> processQueue = new PriorityQueue<>();
    private Semaphore executorLock = new Semaphore(1);

    private Long runningProcess;

    public JormungandExecutor(YggdrasilCore yggdrasilCore, JormungandCore jormungandCore) {
        this.yggdrasilCore = yggdrasilCore;
        this.jormungandCore = jormungandCore;
    }

    @Override
    public void run() {
        yggdrasilCore.logInfo("JormungandExecutor: Starting...");
        try {
            while (yggdrasilCore.isRunningYggdrasil()) {
                if (!processQueue.isEmpty()) {
                    executorLock.acquire();
                    runningProcess = processQueue.remove();

                    JormungandSubProcess subProcess = jormungandCore.getProcess(runningProcess);

                    subProcess.run();

                    yggdrasilCore.logInfo("JormungandExecutor: Process ID" + runningProcess + " finished with EXIT_CODE: " + subProcess.getReturnCode());
                    runningProcess = null;
                    executorLock.release();
                }
                Thread.sleep(100);
            }
        } catch(InterruptedException e) {
            yggdrasilCore.logException(e);
            executorLock.release();
        }
        yggdrasilCore.logInfo("JormungandExecutor: Stopped");
    }

    public void schedule(Long subProcessID) {
        processQueue.add(subProcessID);
    }

    public void shutdownJormungand() {
        if (runningProcess != null) {
            jormungandCore.getProcess(runningProcess).kill();
        }
    }
}
