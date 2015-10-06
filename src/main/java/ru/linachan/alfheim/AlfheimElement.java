package ru.linachan.alfheim;

import org.apache.commons.io.FileUtils;
import ru.linachan.asgard.orm.tables.records.ElementDataRecord;
import ru.linachan.jormungand.JormungandSubProcess;
import ru.linachan.yggdrasil.YggdrasilCore;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AlfheimElement {

    private YggdrasilCore yggdrasilCore;
    private AlfheimCore alfheimCore;
    private ElementDataRecord elementData;

    public AlfheimElement(YggdrasilCore yggdrasilCore, AlfheimCore alfheimCore, ElementDataRecord elementData) {
        this.yggdrasilCore = yggdrasilCore;
        this.alfheimCore = alfheimCore;
        this.elementData = elementData;

        getElement();
    }

    private void getElement() {
        try {
            Path elementDir = Files.createTempDirectory("alfheim");
            Long gitProcessID = yggdrasilCore.getExecutionManager().prepareExecution("git", "clone", elementData.getRepositoryUrl(), elementDir.toString());
            yggdrasilCore.getExecutionManager().scheduleExecution(gitProcessID);
            JormungandSubProcess gitProcess = yggdrasilCore.getExecutionManager().waitFor(gitProcessID);
            if (gitProcess.getReturnCode() == 0) {
                File existingElement = new File(alfheimCore.getElementsPath() + "/" + elementData.getName());
                if (existingElement.exists()) {
                    FileUtils.deleteDirectory(existingElement);
                }

                File newElement = new File(elementDir.toString() + "/" + elementData.getElementPath());

                if (newElement.exists()) {
                    FileUtils.copyDirectory(newElement, existingElement);
                } else {
                    yggdrasilCore.logWarning("AlfheimElement: No element found in: " + newElement.getPath());
                }
            } else {
                yggdrasilCore.logWarning("AlfheimElement: Unable to clone repository: " + elementData.getRepositoryUrl());
            }

            FileUtils.deleteDirectory(elementDir.toFile());
        } catch (IOException e) {
            yggdrasilCore.logException(e);
        }
    }
}
