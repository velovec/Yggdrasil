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

    public JormungandExecutor(YggdrasilCore yggdrasilCore, JormungandCore jormungandCore) {
        this.yggdrasilCore = yggdrasilCore;
        this.jormungandCore = jormungandCore;
    }

    @Override
    public void run() {
        yggdrasilCore.logInfo("Starting JormungandExecutor");
        try {
            while (yggdrasilCore.isRunningYggdrasil()) {
                if (!processQueue.isEmpty()) {
                    executorLock.acquire();
                    Long processID = processQueue.remove();

                    JormungandSubProcess subProcess = jormungandCore.getProcess(processID);

                    subProcess.run();
                    executorLock.release();
                }
                Thread.sleep(1000);
            }
        } catch(InterruptedException e) {
            yggdrasilCore.logException(e);
        }
        yggdrasilCore.logInfo("JormungandExecutor stopped");
    }

    public void schedule(Long subProcessID) {
        processQueue.add(subProcessID);
    }
}
