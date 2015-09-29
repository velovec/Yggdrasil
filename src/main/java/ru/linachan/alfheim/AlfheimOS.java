package ru.linachan.alfheim;

public enum AlfheimOS {

    UBUNTU("ubuntu"),
    CENTOS("centos"),
    FEDORA("fedora"),
    DEBIAN("debian"),
    RHEL("rhel"),

    UBUNTU_TRUSTY("ubuntu", "trusty"),
    DEBIAN_JESSIE("debian", "jessie"),
    DEBIAN_WHEEZY("debian", "wheezy");

    private String operationSystem;
    private String release;

    AlfheimOS(String operationSystem, String release) {
        this.operationSystem = operationSystem;
        this.release = release;
    }

    AlfheimOS(String operationSystem) {
        this.operationSystem = operationSystem;
        this.release = null;
    }

    public String getOperationSystem() {
        return operationSystem;
    }

    public String getRelease() {
        return release;
    }
}
