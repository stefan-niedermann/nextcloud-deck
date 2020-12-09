package it.niedermann.nextcloud.deck.model.ocs;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;

public class Version implements Comparable<Version> {
    private static final Pattern NUMBER_EXTRACTION_PATTERN = Pattern.compile("[0-9]+");
    private static final Version VERSION_1_0_0 = new Version("1.0.0", 1, 0, 0);
    private static final Version VERSION_1_0_3 = new Version("1.0.3", 1, 0, 3);
    private static final Version VERSION_1_3_0 = new Version("1.3.0", 1, 3, 0);
    @Nullable
    private static Version VERSION_MINIMUM_SUPPORTED;

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

    private int getMajor() {
        return major;
    }

    private int getMinor() {
        return minor;
    }

    private int getPatch() {
        return patch;
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
        throw new IllegalArgumentException("could not extract a number from following string: \"" + containsNumbers + "\"");
    }

    @NonNull
    public static Version minimumSupported(@NonNull Context context) {
        if (VERSION_MINIMUM_SUPPORTED == null) {
            final int minimumServerAppMajor = context.getResources().getInteger(R.integer.minimum_server_app_major);
            final int minimumServerAppMinor = context.getResources().getInteger(R.integer.minimum_server_app_minor);
            final int minimumServerAppPatch = context.getResources().getInteger(R.integer.minimum_server_app_patch);
            VERSION_MINIMUM_SUPPORTED = new Version(minimumServerAppMajor + "." + minimumServerAppMinor + "." + minimumServerAppPatch, minimumServerAppMajor, minimumServerAppMinor, minimumServerAppPatch);
        }
        return VERSION_MINIMUM_SUPPORTED;
    }

    public boolean isSupported(@NonNull Context context) {
        return isGreaterOrEqualTo(Version.minimumSupported(context));
    }

    /**
     * @param compare another version object
     * @return -1 if the compared {@link Version} is <strong>higher</strong> than the current version
     * 0 if the compared {@link Version} is equal to the current version
     * 1 if the compared {@link Version} is <strong>lower</strong> than the current version
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

    @NonNull
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
     * {@link DeckComment} API only available starting with {@link Version} 1.0.0-alpha1
     *
     * @return whether or not the server supports the {@link DeckComment} API
     */
    public boolean supportsComments() {
        return isGreaterOrEqualTo(VERSION_1_0_0);
    }

    /**
     * Replying to a {@link DeckComment} does cause synchronization errors because the API expected the
     * <code>parentId</code> to be a {@link String} up until {@link Version} 1.0.3
     * https://github.com/nextcloud/deck/issues/1831#issuecomment-627207849
     *
     * @return whether or not the server supports replying to comments
     */
    public boolean supportsCommentsReplys() {
        return isGreaterOrEqualTo(VERSION_1_0_3);
    }

    /**
     * Before {@link #VERSION_1_3_0} all {@link Attachment}s have been stored in a special folder at the server.
     * Starting with {@link #VERSION_1_3_0} {@link Attachment}s can be stored as regular files, allowing for example to make use of server side thumbnail generation.
     * <p>
     * Since the migration takes a long time, it does not happen on upgrading the server app but step by step via a cronjob.
     * Therefore this method is just an indicator, that it is possible that {@link Attachment}s are stored as files, but it is no guarantee that all {@link Attachment}s already have been migrated to files.
     *
     * @return whether or not the server supports file attachments
     * @see <a href="https://github.com/nextcloud/deck/pull/2638">documentation in PR</a>
     */
    public boolean supportsFileAttachments() {
        return false;
//         TODO depends on https://github.com/nextcloud/deck/pull/2638
//         return isGreaterOrEqualTo(VERSION_1_3_0);
    }

    /**
     * Title max length has been increased from 100 to 255 characters beginning with server {@link Version} 1.0.0
     *
     * @return the number of characters that the title fields of cards allow
     * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/422">issue</a>
     */
    public int getCardTitleMaxLength() {
        return isGreaterOrEqualTo(VERSION_1_0_0)
                ? 255
                : 100;
    }

    /**
     * URL to view a card in the web interface has been changed in {@link Version} 1.0.0
     *
     * @return the id of the string resource which contains the partial URL to open a card in the web UI
     * @see <a href="https://github.com/nextcloud/deck/pull/1977">documentation in PR</a>
     */
    @StringRes
    public int getShareLinkResource() {
        return isGreaterOrEqualTo(VERSION_1_0_0)
                ? R.string.url_fragment_share_card_since_1_0_0
                : R.string.url_fragment_share_card_pre_1_0_0;
    }
}
