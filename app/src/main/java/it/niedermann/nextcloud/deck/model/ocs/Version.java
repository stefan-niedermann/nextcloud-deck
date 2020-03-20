package it.niedermann.nextcloud.deck.model.ocs;

public class Version implements Comparable<Version> {
    private String originalVersion;
    private int major;
    private int minor;
    private int patch;

    public Version(String originalVersion, int major, int minor, int patch) {
        this(major, minor, patch);
        this.originalVersion = originalVersion;
    }

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
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

    public int getPatch() {
        return patch;
    }

    public void setPatch(int patch) {
        this.patch = patch;
    }

    public boolean isGreaterOrEqualTo(Version v) {
        return compareTo(v) >= 0;
    }

    public String getOriginalVersion() {
        return originalVersion;
    }

    public void setOriginalVersion(String originalVersion) {
        this.originalVersion = originalVersion;
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
        } else if (o.getPatch() > getPatch()) {
            return -1;
        } else if (o.getPatch() < getPatch()) {
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Version{" +
                "originalVersion='" + originalVersion + '\'' +
                ", major=" + major +
                ", minor=" + minor +
                ", patch=" + patch +
                '}';
    }
}
