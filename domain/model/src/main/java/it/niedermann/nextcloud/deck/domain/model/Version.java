package it.niedermann.nextcloud.deck.domain.model;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class Version implements Serializable, Comparable<Version> {

    private static final Pattern NUMBER_EXTRACTION_PATTERN = Pattern.compile("\\d+");
    private final String version;
    private final int major, minor, patch;

    public Version(String version, int major, int minor, int patch) {
        this.version = version;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static Version parse(String version) {
        final int major, minor, patch;
        final String[] split = version.split("\\.");
        if (split.length > 0) {
            major = extractNumber(split[0]);
            if (split.length > 1) {
                minor = extractNumber(split[1]);
                if (split.length > 2) {
                    patch = extractNumber(split[2]);
                } else {
                    patch = 0;
                }
            } else {
                throw new IllegalArgumentException("Could not parse version " + version);
            }
        } else {
            throw new IllegalArgumentException("Could not parse version " + version);
        }
        return new Version(version, major, minor, patch);
    }

    private static int extractNumber(String containsNumbers) {
        final var matcher = NUMBER_EXTRACTION_PATTERN.matcher(containsNumbers);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        throw new IllegalArgumentException("Could not extract a number from: " + containsNumbers);
    }

    public String getVersion() {
        return version;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    @Override
    public int compareTo(Version o) {
        if (major < o.getMajor()) {
            return -1;
        } else if (major > o.getMajor()) {
            return 1;
        }

        if (minor < o.getMinor()) {
            return -1;
        } else if (minor > o.getMinor()) {
            return 1;
        }

        if (patch < o.getPatch()) {
            return -1;
        } else if (patch > o.getPatch()) {
            return 1;
        }

        return 0;
    }

    public boolean isGreaterThan(Version o) {
        return compareTo(o) > 0;
    }

    public boolean isGreaterThanOrEqual(Version o) {
        return compareTo(o) >= 0;
    }

    public boolean isLessThan(Version o) {
        return compareTo(o) < 0;
    }

    public boolean isLessThanOrEqual(Version o) {
        return compareTo(o) <= 0;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%d.%d.%d", major, minor, patch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version1 = (Version) o;
        return major == version1.major && minor == version1.minor && patch == version1.patch && Objects.equals(version, version1.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, major, minor, patch);
    }
}
