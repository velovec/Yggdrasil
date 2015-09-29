package ru.linachan.alfheim;

public enum AlfheimArchitecture {

    AMD64("amd64"),
    I386("i386"),
    ARMHF("armhf");

    private final String arch;

    AlfheimArchitecture(String arch) {
        this.arch = arch;
    }

    public String getArch() {
        return arch;
    }
}
