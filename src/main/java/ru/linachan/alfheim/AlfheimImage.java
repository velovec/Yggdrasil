package ru.linachan.alfheim;

import java.util.*;

public class AlfheimImage {

    private AlfheimOS operationSystem;
    private List<String> elements = new ArrayList<>();
    private String imageName;
    private AlfheimImageType imageType;
    private AlfheimArchitecture imageArch;
    private Map<String, String> parameters = new HashMap<>();

    public AlfheimImage(String imageName, AlfheimOS operationSystem, AlfheimArchitecture imageArch, AlfheimImageType imageType) {
        this.imageName = imageName;
        this.operationSystem = operationSystem;
        this.imageType = imageType;
        this.imageArch = imageArch;

        this.elements.add("vm");
        this.elements.add(operationSystem.getOperationSystem());
    }

    public void addElement(String... elements) {
        Collections.addAll(this.elements, elements);
    }

    public void addParameter(String key, String value) {
        parameters.put(key, value);
    }

    public AlfheimOS getOperationSystem() {
        return operationSystem;
    }

    public String getImageName() {
        return imageName;
    }

    public AlfheimImageType getImageType() {
        return imageType;
    }

    public List<String> getElementList() {
        return elements;
    }

    public AlfheimArchitecture getImageArch() {
        return imageArch;
    }

    public List<String> buildCommandLine(Boolean useSudo) {
        List<String> commandLine = new ArrayList<>();

        if (useSudo)
            Collections.addAll(commandLine, "sudo", "-E");

        commandLine.add("disk-image-create");

        Collections.addAll(commandLine, "-a", imageArch.getArch());
        Collections.addAll(commandLine, "-t", imageType.getImageType());
        Collections.addAll(commandLine, "-o", imageName);
        for (String elementName : elements) {
            commandLine.add(elementName);
        }

        return commandLine;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
