package ru.linachan.alfheim;

public enum AlfheimImageType {
    QCOW2("qcow2"),
    TAR("tar"),
    VHD("vhd"),
    RAW("raw"),
    DOCKER("docker");

    private String imageType;

    AlfheimImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageType() {
        return imageType;
    }
}
