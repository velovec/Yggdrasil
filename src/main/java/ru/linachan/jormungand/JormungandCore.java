package ru.linachan.jormungand;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.util.*;

public class JormungandCore {

    private YggdrasilCore core;
    private Map<Long, JormungandSubProcess> processList = new HashMap<>();
    private JormungandExecutor processExecutor;

    public JormungandCore(YggdrasilCore yggdrasilCore) {
        this.core = yggdrasilCore;
        this.processExecutor = new JormungandExecutor(core, this);
        new Thread(this.processExecutor).start();
    }

    private Long generateProcessID() {
        Random randomGenerator = new Random();
        while (true) {
            Long pid = randomGenerator.nextLong();
            if (!processList.containsKey(pid))
                return pid;
        }
    }

    public JormungandSubProcess getProcess(Long processID) {
        if (processList.containsKey(processID))
            return processList.get(processID);
        return null;
    }

    public JormungandSubProcess waitFor(Long processID) {
        JormungandSubProcess process = getProcess(processID);
        while (!process.isFinished()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                this.core.logException(e);
            }
        }
        return process;
    }

    public Long scheduleExecution(String... cmd) {
        JormungandSubProcess subProcess = new JormungandSubProcess(this.core, cmd);
        Long subProcessID = generateProcessID();

        processList.put(subProcessID, subProcess);
        processExecutor.schedule(subProcessID);

        return subProcessID;
    }

    public boolean execute_tests() {
        Long pid = scheduleExecution("ls", "-l");

        JormungandSubProcess process = waitFor(pid);

        Integer retVal = process.getReturnCode();

        return retVal == 0;
    }
}
