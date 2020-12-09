package it.niedermann.nextcloud.deck.util;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.URL;

import it.niedermann.nextcloud.deck.model.Account;

public class ProjectUtil {

    private ProjectUtil() {
    }

    @NonNull
    public static Uri getResourceUri(@NonNull Account account, @NonNull String link) throws IllegalArgumentException {
        try {
            // Assume link contains a fully qualified Uri including host
            final URL u = new URL(link);
            return Uri.parse(u.toString());
        } catch (Throwable linkIsNotQualified) {
            try {
                // Assume link is a absolute path that needs to be concatenated with account url for a complete Uri
                final URL u = new URL(account.getUrl() + link);
                return Uri.parse(u.toString());
            } catch (Throwable throwable) {
                throw new IllegalArgumentException("Could not parse " + Uri.class.getSimpleName() + ": " + link, throwable);
            }
        }
    }

    /**
     * extracts the values of board- and card-ID from url.
     * Depending on what kind of url it gets, it will return a long[] of length 1 or 2:
     * If the url contains both values, you'll get 2, if it contains only the board, you'll get 1.
     * <p>
     * The order is fixed here: [boardId, cardId]
     *
     * @param url to extract from
     * @return extracted and parsed values as long[] with length 1-2
     */
    public static long[] extractBoardIdAndCardIdFromUrl(@Nullable String url) throws IllegalArgumentException {
        if (url == null) {
            throw new IllegalArgumentException("provided url is null");
        }
        url = url.trim();
        if (url.length() == 0) {
            throw new IllegalArgumentException("trimmed url is empty");
        }
        // extract important part
        String[] splitByPrefix = url.split(".*(index\\.php/)?apps/deck(/#)?/board/");
        // split into board- and card part
        if (splitByPrefix.length < 2) {
            throw new IllegalArgumentException("This URL doesn't seem to be an URL containing the boardId: \"" + url + "\"");
        }
        String[] splitBySeparator = splitByPrefix[1].split("/card/");

        // remove any unexpected stuff
        if (splitBySeparator.length > 1 && splitBySeparator[1].contains("/")) {
            splitBySeparator[1] = splitBySeparator[1].split("/")[0];
        }
        if (splitBySeparator.length > 0 && splitBySeparator[0].contains("/")) {
            splitBySeparator[0] = splitBySeparator[0].split("/")[0];
        }

        if (splitBySeparator.length < 1) {
            throw new IllegalArgumentException("This URL doesn't seem to be an URL containing the boardId: \"" + url + "\"");
        }

        // return result
        long boardId = Long.parseLong(splitBySeparator[0]);
        if (boardId < 1) {
            throw new IllegalArgumentException("Invalid boardId \"" + boardId + "\" for url \"" + url + "\".");
        }
        if (splitBySeparator.length == 1) {
            return new long[]{boardId};
        } else if (splitBySeparator.length == 2) {
            long cardId = Long.parseLong(splitBySeparator[1]);
            if (cardId > 0) {
                return new long[]{boardId, cardId};
            } else {
                return new long[]{boardId};
            }
        } else {
            throw new IllegalArgumentException("could not parse URL for boardId and/or cardId: \"" + url + "\"");
        }
    }
}
