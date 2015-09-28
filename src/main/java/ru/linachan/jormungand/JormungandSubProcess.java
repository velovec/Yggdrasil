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
    private JormungandCore jormungandCore;
    private YggdrasilCore yggdrasilCore;
    private Map<String, String> processEnvironment;
    private InputStream processOutput;

    public JormungandSubProcess(YggdrasilCore yggdrasilCore, JormungandCore jormungandCore, String... cmd) {
        this.jormungandCore = jormungandCore;
        this.yggdrasilCore = yggdrasilCore;
        this.processBuilder = new ProcessBuilder(cmd);
        this.processEnvironment = processBuilder.environment();

        this.processBuilder.redirectErrorStream(true);
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

    public Integer run() {
        Integer returnCode = 1;

        try {
            jormungandCore.acquire_lock();
            Process proc = processBuilder.start();

            processOutput = proc.getInputStream();
            proc.waitFor();

            returnCode = proc.exitValue();
        } catch(InterruptedException | IOException e) {
            this.yggdrasilCore.logException(e);
        } finally {
            jormungandCore.release_lock();
        }

        return returnCode;
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
}
