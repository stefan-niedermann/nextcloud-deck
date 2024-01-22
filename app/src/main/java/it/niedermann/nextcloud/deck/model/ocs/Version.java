package it.niedermann.nextcloud.deck.model.ocs;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;

public class Version implements Comparable<Version> {
    private static final Pattern NUMBER_EXTRACTION_PATTERN = Pattern.compile("[0-9]+");
    private static final Version VERSION_0_6_4 = new Version("0.6.4", 0, 6, 4);
    private static final Version VERSION_1_0_0 = new Version("1.0.0", 1, 0, 0);
    private static final Version VERSION_1_0_3 = new Version("1.0.3", 1, 0, 3);
    private static final Version VERSION_1_3_0 = new Version("1.3.0", 1, 3, 0);
    private static final Version VERSION_1_12_0 = new Version("1.12.0", 1, 12, 0);
    private static final Version VERSION_1_12_2 = new Version("1.12.2", 1, 12, 2);

    private String originalVersion = "?";
    private final int major;
    private final int minor;
    private final int patch;

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

    public static Version of(String versionString) {
        int major = 0, minor = 0, patch = 0;
        if (versionString != null) {
            final String[] split = versionString.split("\\.");
            if (split.length > 0) {
                major = extractNumber(split[0]);
                if (split.length > 1) {
                    minor = extractNumber(split[1]);
                    if (split.length > 2) {
                        patch = extractNumber(split[2]);
                    }
                }
            }
        }
        return new Version(versionString, major, minor, patch);
    }

    private static int extractNumber(String containsNumbers) {
        Matcher matcher = NUMBER_EXTRACTION_PATTERN.matcher(containsNumbers);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        throw new IllegalArgumentException("could not extract a number from following string: \"" + containsNumbers + "\"");
    }

    @NonNull
    public static Version minimumSupported() {
        return VERSION_0_6_4;
    }

    public boolean isSupported() {
        return isGreaterOrEqualTo(minimumSupported());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Version version = (Version) o;
        return compareTo(version) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalVersion, major, minor, patch);
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
     * {@link DeckComment} API only available starting with {@link Version} <code>1.0.0-alpha1</code>
     *
     * @return whether or not the server supports the {@link DeckComment} API
     */
    public boolean supportsComments() {
        return isGreaterOrEqualTo(VERSION_1_0_0);
    }

    /**
     * Replying to a {@link DeckComment} does cause synchronization errors because the API expected the
     * <code>parentId</code> to be a {@link String} up until {@link #VERSION_1_0_3}
     *
     * @return whether or not the server supports replying to comments
     * @see <a href="https://github.com/nextcloud/deck/issues/1831#issuecomment-627207849">Deck server issue #1831</a>
     */
    public boolean supportsCommentsReplies() {
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
        return isGreaterOrEqualTo(VERSION_1_3_0);
    }

    public boolean supportsDeletingFileAttachments() {
        return isGreaterOrEqualTo(VERSION_1_12_2);
    }

    /**
     * Cards started to have an additional property called <a href="https://github.com/nextcloud/deck/pull/4137"><code>done</code></a> with version <a href="https://github.com/nextcloud/deck/releases/tag/v1.12.0">{@link #VERSION_1_12_0}</a> of the Deck server app.
     * However, there was an <a href="https://github.com/nextcloud/deck/issues/534#issuecomment-1892061055">issue that would have required to call a second endpoint when marking a card as <code>undone</code></a> which was <a href="https://github.com/nextcloud/deck/pull/5491">fixed</a> in {@link #VERSION_1_12_2}.
     * We therefore support the <code>done</code> property only starting with {@link #VERSION_1_12_2}.
     *
     * @return whether or not the server supports the {@link Card#getDone()} state
     * @see <a href="https://github.com/nextcloud/deck/issues/534">Deck server issue #534</a>
     */
    public boolean supportsDone() {
        return isGreaterOrEqualTo(VERSION_1_12_2);
    }

    /**
     * Title max length has been increased from <code>100</code> to <code>255</code> characters beginning with server {@link #VERSION_1_0_0}
     *
     * @return the number of characters that the title fields of cards allow
     * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/422">issue</a>
     */
    public int getCardTitleMaxLength() {
        return isGreaterOrEqualTo(VERSION_1_0_0) ? 255 : 100;
    }

    /**
     * The first response structure of the very first call to at least the <code>/boards</code> endpoint of the Deck API can be different compared to all following calls.
     * This behavior is tracked in an upstream issue and might be resolved in the future with a specific version.
     *
     * @return whether or not it is needed to make one request that must be ignored due to a different data structure before performing any other requests.
     * @see <a href="https://github.com/nextcloud/deck/issues/3229">issue</a>
     */
    public boolean firstCallHasDifferentResponseStructure() {
        return true;
    }

    /**
     * URL to view a card in the web interface has been changed in {@link #VERSION_1_0_0}
     *
     * @return the id of the string resource which contains the partial URL to open a card in the web UI
     * @see <a href="https://github.com/nextcloud/deck/pull/1977">documentation in PR</a>
     */
    @StringRes
    public int getShareLinkResource() {
        if (isGreaterOrEqualTo(VERSION_1_12_0)) { // Probably even earlier, but there are likely redirects
            return R.string.url_fragment_share_card_since_1_12_0;
        } else if (isGreaterOrEqualTo(VERSION_1_0_0)) {
            return R.string.url_fragment_share_card_since_1_0_0;
        } else {
            return R.string.url_fragment_share_card_pre_1_0_0;
        }
    }
}
