package it.niedermann.nextcloud.deck.util.glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.load.model.LazyHeaders;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.Map;

import it.niedermann.nextcloud.deck.model.Account;

public class SingleSignOnOriginHeader implements Headers {

    private LazyHeaders headers;

    /**
     * Use this header and set the {@link SingleSignOnAccount} name property as value
     * Format of the value needs to be
     */
    public static final String X_HEADER_SSO_ACCOUNT_NAME = "X-SSO-Account-Name";

    /**
     * Use this as {@link Headers} if you want to do a {@link Glide} request for an {@link SingleSignOnAccount} which is not set by {@link SingleAccountHelper} as current {@link SingleSignOnAccount}.
     *
     * @param account Account from which host the request should be fired
     */
    public SingleSignOnOriginHeader(@NonNull Account account) {
        this.headers = new LazyHeaders.Builder().addHeader(X_HEADER_SSO_ACCOUNT_NAME, account.getName()).build();
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.headers.getHeaders();
    }
}
