package ru.linachan.jormungand;

import ru.linachan.util.Pair;
import ru.linachan.yggdrasil.YggdrasilCore;
import ru.linachan.yggdrasil.component.YggdrasilComponent;

import java.util.*;

public class JormungandCore extends YggdrasilComponent {

    private Map<Long, JormungandSubProcess> processList = new HashMap<>();
    private JormungandExecutor processExecutor;

    @Override
    protected void onInit() {
        processExecutor = new JormungandExecutor();
        core.startService(processExecutor);
    }

    @Override
    protected void onShutdown() {

    }

    @Override
    public boolean executeTests() {
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
                core.logException(e);
            }
        }
        return process;
    }

    public Long prepareExecution(String... cmd) {
        JormungandSubProcess subProcess = new JormungandSubProcess(core, cmd);
        Long subProcessID = generateProcessID();

        processList.put(subProcessID, subProcess);
        return subProcessID;
    }

    public Long prepareExecution(List<String> cmd) {
        JormungandSubProcess subProcess = new JormungandSubProcess(core, cmd);
        Long subProcessID = generateProcessID();

        processList.put(subProcessID, subProcess);
        return subProcessID;
    }

    public void scheduleExecution(Long subProcessID) {
        processExecutor.schedule(subProcessID);
    }
}
