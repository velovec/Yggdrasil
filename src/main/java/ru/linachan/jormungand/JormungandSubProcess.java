package ru.linachan.jormungand;

import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JormungandSubProcess {

    private ProcessBuilder processBuilder;
    private YggdrasilCore yggdrasilCore;
    private Map<String, String> processEnvironment;
    private InputStream processOutput;
    private Integer returnCode;

    private JormungandSubProcessState processState;

    public JormungandSubProcess(YggdrasilCore yggdrasilCore, String... cmd) {
        this.yggdrasilCore = yggdrasilCore;
        this.processBuilder = new ProcessBuilder(cmd);
        this.processEnvironment = processBuilder.environment();

        this.processBuilder.redirectErrorStream(true);

        this.processState = JormungandSubProcessState.READY;
    }

    public void clearEnvironment() {
        this.processEnvironment.clear();
    }

    public void setEnvironment(String key, String value) {
        this.processEnvironment.put(key, value);
    }

    public void setEnvironment(Map<String, String> map) {
        this.processEnvironment.putAll(map);
    }

    public void unsetEnvironment(String key) {
        this.processEnvironment.remove(key);
    }

    public void setWorkingDirectory(File workingDirectory) {
        this.processBuilder.directory(workingDirectory);
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.processBuilder.directory(new File(workingDirectory));
    }

    public void run() {
        try {
            processState = JormungandSubProcessState.WAITING;

            Process proc = processBuilder.start();
            processState = JormungandSubProcessState.RUNNING;

            processOutput = proc.getInputStream();

            proc.waitFor();

            returnCode = proc.exitValue();
            processState = JormungandSubProcessState.FINISHED;
        } catch(InterruptedException | IOException e) {
            processState = JormungandSubProcessState.ERROR;
            returnCode = 1;
            this.yggdrasilCore.logException(e);
        }
    }

    public List<String> getProcessOutput() {
        List<String> outputLog = new ArrayList<String>();

        try {
            Integer bytesAvailable = processOutput.available();

            byte[] rawOutput = new byte[bytesAvailable];

            processOutput.read(rawOutput);

            Collections.addAll(outputLog, new String(rawOutput).split("\\n"));
        } catch (IOException e) {
            this.yggdrasilCore.logException(e);
        }

        return outputLog;
    }

    public JormungandSubProcessState getState() {
        return processState;
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public boolean isFinished() {
        return (processState == JormungandSubProcessState.FINISHED)||(processState == JormungandSubProcessState.ERROR);
    }
}
