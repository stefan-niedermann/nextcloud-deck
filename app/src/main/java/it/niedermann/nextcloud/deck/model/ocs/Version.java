package it.niedermann.nextcloud.deck.model.ocs;

public class Version {
    private int major;
    private int minor;
    private int micro;

    public Version(int major, int minor, int micro) {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getMicro() {
        return micro;
    }

    public void setMicro(int micro) {
        this.micro = micro;
    }
}
