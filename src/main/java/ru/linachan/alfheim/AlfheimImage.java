package ru.linachan.alfheim;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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

    public AlfheimImage(JSONObject imageData) {
        this.imageName = (String) imageData.get("name");

        for (Object element : (JSONArray) imageData.get("elements")) {
            this.elements.add((String) element);
        }

        String imageArch = (String) imageData.get("arch");
        String imageType = (String) imageData.get("type");
        String osFamily = (String) ((JSONObject)imageData.get("os")).get("family");
        String osVersion = (String) ((JSONObject)imageData.get("os")).get("release");

        this.imageArch = AlfheimArchitecture.valueOf(imageArch.toUpperCase());
        this.imageType = AlfheimImageType.valueOf(imageType.toUpperCase());
        this.operationSystem = AlfheimOS.valueOf((osFamily + "_" + osVersion).toUpperCase());
        for (Object key : ((JSONObject) imageData.get("env")).keySet()) {
            Object value = ((JSONObject) imageData.get("env")).get(key);
            parameters.put((String) key, (String) value);
        }
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
