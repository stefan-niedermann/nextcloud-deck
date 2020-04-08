package it.niedermann.nextcloud.deck.model.ocs;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {
    private static final Pattern NUMBER_EXTRACTION_PATTERN = Pattern.compile("[0-9]+");

    private String originalVersion = "?";
    private int major = 0;
    private int minor = 0;
    private int patch = 0;

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

    public static Version of(String versionString) {
        int major = 0, minor = 0, micro = 0;
        if (versionString != null) {
            String[] split = versionString.split("\\.");
            if (split.length > 0){
                major = extractNumber(split[0]);
                if (split.length > 1) {
                    minor = extractNumber(split[1]);
                    if (split.length > 2) {
                        micro = extractNumber(split[2]);
                    }
                }
            }
        }
        return new Version(versionString, major, minor, micro);
    }

    private static int extractNumber(String containsNumbers) {
        Matcher matcher = NUMBER_EXTRACTION_PATTERN.matcher(containsNumbers);
        if (matcher.find()){
            return Integer.parseInt(matcher.group());
        }
        return 0;
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
