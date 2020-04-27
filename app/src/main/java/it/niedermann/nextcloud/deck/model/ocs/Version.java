package it.niedermann.nextcloud.deck.model.ocs;

import android.content.Context;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.niedermann.nextcloud.deck.R;

public class Version implements Comparable<Version> {
    private static final Pattern NUMBER_EXTRACTION_PATTERN = Pattern.compile("[0-9]+");
    private static final Version VERSION_1_0_0 = new Version("1.0.0", 1, 0, 0);

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
            if (split.length > 0) {
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
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return 0;
    }

    public static Version minimumSupported(@NonNull Context context) {
        final int minimumServerAppMajor = context.getResources().getInteger(R.integer.minimum_server_app_major);
        final int minimumServerAppMinor = context.getResources().getInteger(R.integer.minimum_server_app_minor);
        final int minimumServerAppPatch = context.getResources().getInteger(R.integer.minimum_server_app_patch);
        return new Version(minimumServerAppMajor, minimumServerAppMinor, minimumServerAppPatch);
    }

    public boolean isSupported(@NonNull Context context) {
        return isGreaterOrEqualTo(Version.minimumSupported(context));
    }

    /**
     * @param compare another version object
     * @return -1 if the compared version is <strong>higher</strong> than the current version
     * 0 if the compared version is equal to the current version
     * 1 if the compared version is <strong>lower</strong> than the current version
     */
    @Override
    public int compareTo(@NonNull Version compare) {
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

    /**
     * Comments API only available starting with version 1.0.0-alpha1
     *
     * @return whether or not the server supports the Comments API
     */
    public boolean supportsComments() {
        return isGreaterOrEqualTo(VERSION_1_0_0);
    }

    /**
     * Title max length has been increased from 100 to 255 characters beginning with server version 1.0.0
     *
     * @return the number of characters that the title fields of cards allow
     * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/422">issue</a>
     */
    public int getCardTitleMaxLength() {
        return isGreaterOrEqualTo(VERSION_1_0_0)
                ? 255
                : 100;
    }
}
