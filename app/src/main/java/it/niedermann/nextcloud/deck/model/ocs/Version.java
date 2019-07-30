package it.niedermann.nextcloud.deck.model.ocs;

public class Version implements Comparable<Version>{
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

    public boolean isGreaterOrEqualTo(Version v){
        return compareTo(v) >= 0;
    }

    @Override
    public int compareTo(Version o) {
        if (o.getMajor() > getMajor()) {
            return -1;
        } else if (o.getMajor() < getMajor()) {
            return 1;
        } else if (o.getMinor() > getMinor()) {
            return -1;
        } else if (o.getMinor() < getMinor()) {
            return 1;
        } else if (o.getMicro() > getMicro()) {
            return -1;
        } else if (o.getMicro() < getMicro()) {
            return 1;
        }
        return 0;
    }
}
