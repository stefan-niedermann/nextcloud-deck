package it.niedermann.nextcloud.deck.model.ocs;

import org.jetbrains.annotations.NotNull;

public class Version implements Comparable<Version>{
    private String originalVersion;
    private int major;
    private int minor;
    private int patch;

    public Version(String originalVersion, int major, int minor, int patch) {
        this.originalVersion = originalVersion;
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

    public boolean isGreaterOrEqualTo(Version v){
        return compareTo(v) >= 0;
    }

    public String getOriginalVersion() {
        return originalVersion;
    }

    public void setOriginalVersion(String originalVersion) {
        this.originalVersion = originalVersion;
    }

    /**
     *
     * @param compare another version object
     * @return -1 if the compared version is <strong>higher</strong> than the current version
     *          0 if the compared version is equal to the current version
     *          1 if the compared version is <strong>lower</strong> than the current version
     */
    @Override
    public int compareTo(Version compare) {
        if (compare.getMajor() > getMajor()) {
            return -1;
        } else if (compare.getMajor() < getMajor()) {
            return 1;
        } else if (compare.getMinor() > getMinor()) {
            return -1;
        } else if (compare.getMinor() < getMinor()) {
            return 1;
        } else if (compare.getPatch() > getPatch()) {
            return -1;
        } else if (compare.getPatch() < getPatch()) {
            return 1;
        }
        return 0;
    }

    @NotNull
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
