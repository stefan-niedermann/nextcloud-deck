package it.niedermann.nextcloud.deck.feature.shared.util;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;

import it.niedermann.nextcloud.deck.shared.model.Account;
import it.niedermann.nextcloud.sso.glide.SingleSignOnUrl;

public class AvatarUtil {

    private static final AvatarUtil INSTANCE = new AvatarUtil();

    private AvatarUtil() {

    }

    public static AvatarUtil getInstance() {
        return INSTANCE;
    }

    /**
     * @return The {@link #getAvatarUrl(Account, int, String)} of this {@link Account}
     */
    public GlideUrl getAvatarUrl(@NonNull Account account, @Px int size) {
        return getAvatarUrl(account, account.getUserName(), size);
    }

    /**
     * @return a {@link GlideUrl} to fetch the avatar of the given <code>userName</code> from the instance of this {@link Account} via {@link Glide}.
     */
    public GlideUrl getAvatarUrl(@NonNull Account account, String userName, @Px int size) {
        return getAvatarUrl(account.getAccountName(), account.getUrl(), userName, size);
    }

    /**
     * @return a {@link GlideUrl} to fetch the avatar of the given <code>userName</code> from the instance of this {@link Account} via {@link Glide}.
     */
    public GlideUrl getAvatarUrl(@NonNull String accountName, String url, @NonNull String userName, @Px int size) {
        return new SingleSignOnUrl(accountName, url + "/index.php/avatar/" + Uri.encode(userName) + "/" + size);
    }
}
