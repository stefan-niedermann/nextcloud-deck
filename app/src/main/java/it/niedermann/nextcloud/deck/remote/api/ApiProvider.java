package it.niedermann.nextcloud.deck.remote.api;

import android.content.Context;

import androidx.annotation.NonNull;

import com.nextcloud.android.sso.api.NextcloudAPI;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import retrofit2.NextcloudRetrofitApiBuilder;

/**
 * Created by david on 26.05.17.
 */
public class ApiProvider {

    private static final String DECK_API_ENDPOINT = "/index.php/apps/deck/api/";
    private static final String NC_API_ENDPOINT = "/ocs/v2.php/";

    private DeckAPI deckAPI;
    private NextcloudServerAPI nextcloudAPI;
    @NonNull
    private final Context context;
    private final SingleSignOnAccount ssoAccount;

    public ApiProvider(@NonNull Context context, @NonNull SingleSignOnAccount ssoAccount) {
        this.context = context;
        this.ssoAccount = ssoAccount;
    }

    public synchronized void initSsoApi(@NonNull final NextcloudAPI.ApiConnectedListener callback) {
        if (this.deckAPI == null) {
            final NextcloudAPI nextcloudAPI = new NextcloudAPI(context, ssoAccount, GsonConfig.getGson(), callback);
            this.deckAPI = new NextcloudRetrofitApiBuilder(nextcloudAPI, DECK_API_ENDPOINT).create(DeckAPI.class);
            this.nextcloudAPI = new NextcloudRetrofitApiBuilder(nextcloudAPI, NC_API_ENDPOINT).create(NextcloudServerAPI.class);
        }
    }

    public DeckAPI getDeckAPI() {
        return deckAPI;
    }

    public NextcloudServerAPI getNextcloudAPI() {
        return nextcloudAPI;
    }

}
