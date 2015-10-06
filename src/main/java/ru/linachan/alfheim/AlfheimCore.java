package ru.linachan.alfheim;

import org.jooq.DSLContext;
import ru.linachan.asgard.orm.tables.ElementData;
import ru.linachan.asgard.orm.tables.records.ElementDataRecord;
import ru.linachan.jormungand.JormungandCore;
import ru.linachan.jormungand.JormungandSubProcess;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlfheimCore {

    private YggdrasilCore yggdrasilCore;
    private JormungandCore jormungandCore;
    private String elementsPath;
    private Boolean useSudo;
    private String workingDirectory;

    public AlfheimCore(YggdrasilCore yggdrasilCore) {
        this.yggdrasilCore = yggdrasilCore;
        this.jormungandCore = yggdrasilCore.getExecutionManager();

        this.elementsPath = yggdrasilCore.getConfig("AlfheimElementsPath", "elements");
        this.useSudo = Boolean.parseBoolean(yggdrasilCore.getConfig("AlfheimUseSudo", "true"));
        this.workingDirectory = yggdrasilCore.getConfig("AlfheimImagePath", ".");
    }

    public Long buildImage(AlfheimImage image) {
        List<String> commandLine = image.buildCommandLine(useSudo);

        Long processID = jormungandCore.prepareExecution(commandLine);
        yggdrasilCore.logInfo("Executing: " + commandLine);

        if (image.getOperationSystem().getRelease() != null)
            jormungandCore.getProcess(processID).setEnvironment("DIB_RELEASE", image.getOperationSystem().getRelease());
        jormungandCore.getProcess(processID).setEnvironment("ELEMENTS_PATH", elementsPath);
        jormungandCore.getProcess(processID).setEnvironment(image.getParameters());
        jormungandCore.getProcess(processID).setWorkingDirectory(workingDirectory);
        jormungandCore.getProcess(processID).tagProcess("alfheimBuild");

        jormungandCore.scheduleExecution(processID);

        return processID;
    }

    public boolean execute_tests() {
        Long processID = jormungandCore.prepareExecution("which", "disk-image-create");
        jormungandCore.scheduleExecution(processID);
        JormungandSubProcess process = jormungandCore.waitFor(processID);

        if (process.getReturnCode() > 0) {
            yggdrasilCore.logWarning("diskimage-builder not installed!");
            yggdrasilCore.logWarning("EXIT_CODE: " + process.getReturnCode());
            for (String line : process.getProcessOutput()) {
                yggdrasilCore.logWarning("OUT: " + line);
            }
            return false;
        }

        if (!new File(elementsPath).isDirectory()) {
            yggdrasilCore.logWarning("elementsPath doesn't exist!");
            return false;
        }

        if (!new File(workingDirectory).isDirectory()) {
            yggdrasilCore.logWarning("imagePath doesn't exist!");
            return false;
        }

        return true;
    }

    public Map<String, AlfheimElement> getElements() {
        Map<String, AlfheimElement> elements = new HashMap<>();

        DSLContext ctx = yggdrasilCore.getDBManager().getContext();

        for (ElementDataRecord result : ctx.selectFrom(ElementData.ELEMENT_DATA).fetch()) {
            AlfheimElement element = new AlfheimElement(yggdrasilCore, this, result);
        }

        return elements;
    }

    public String getElementsPath() {
        return elementsPath;
    }
}
