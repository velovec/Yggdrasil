package ru.linachan.jormungand;

import ru.linachan.util.Pair;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.util.*;

public class JormungandCore {

    private YggdrasilCore core;
    private Map<Long, JormungandSubProcess> processList = new HashMap<>();
    private JormungandExecutor processExecutor;

    public JormungandCore(YggdrasilCore yggdrasilCore) {
        this.core = yggdrasilCore;
        this.core.logInfo("Initializing Jormungand Shell Executor...");

        this.processExecutor = new JormungandExecutor(core, this);
        new Thread(this.processExecutor).start();
    }

    private Long generateProcessID() {
        Random randomGenerator = new Random();
        while (true) {
            Long pid = Math.abs(randomGenerator.nextLong());
            if (!processList.containsKey(pid))
                return pid;
        }
    }

    public JormungandSubProcess getProcess(Long processID) {
        if (processList.containsKey(processID))
            return processList.get(processID);
        return null;
    }

    public List<Pair<Long, JormungandSubProcess>> getProcessesByTag(String tag) {
        List<Pair<Long, JormungandSubProcess>> processes = new ArrayList<>();
        for (Long processID : processList.keySet()) {
            if (processList.get(processID).hasTag(tag)) {
                processes.add(new Pair<>(processID, processList.get(processID)));
            }
        }
        return processes;
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

    public Long prepareExecution(String... cmd) {
        JormungandSubProcess subProcess = new JormungandSubProcess(this.core, cmd);
        Long subProcessID = generateProcessID();

        processList.put(subProcessID, subProcess);
        return subProcessID;
    }

    public Long prepareExecution(List<String> cmd) {
        JormungandSubProcess subProcess = new JormungandSubProcess(this.core, cmd);
        Long subProcessID = generateProcessID();

        processList.put(subProcessID, subProcess);
        return subProcessID;
    }

    public void scheduleExecution(Long subProcessID) {
        processExecutor.schedule(subProcessID);
    }

    public void shutdownJormungand() {
        processExecutor.shutdownJormungand();
    }

    public boolean execute_tests() {
        Long pid = prepareExecution("ls", "-l");

        scheduleExecution(pid);

        JormungandSubProcess process = waitFor(pid);

        Integer retVal = process.getReturnCode();

        if (retVal > 0) {
            core.logWarning("EXIT_CODE: " + retVal);
            for (String line : process.getProcessOutput()) {
                core.logWarning("OUT: " + line);
            }
        }

        return retVal == 0;
    }
}
