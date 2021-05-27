package it.niedermann.nextcloud.deck.api;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.api.NextcloudAPI;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import it.niedermann.nextcloud.deck.DeckLog;
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
    @Nullable
    private final String ssoAccountName;
    private SingleSignOnAccount ssoAccount;

    public ApiProvider(@NonNull Context context, @Nullable String ssoAccountName) {
        this.context = context;
        this.ssoAccountName = ssoAccountName;
        setAccount();
    }

    public synchronized void initSsoApi(@NonNull final NextcloudAPI.ApiConnectedListener callback) {
        if(this.deckAPI == null) {
            final NextcloudAPI nextcloudAPI = new NextcloudAPI(context, ssoAccount, GsonConfig.getGson(), callback);
            this.deckAPI = new NextcloudRetrofitApiBuilder(nextcloudAPI, DECK_API_ENDPOINT).create(DeckAPI.class);
            this.nextcloudAPI = new NextcloudRetrofitApiBuilder(nextcloudAPI, NC_API_ENDPOINT).create(NextcloudServerAPI.class);
        }
    }

    private void setAccount() {
        try {
            if (ssoAccountName == null) {
                this.ssoAccount = SingleAccountHelper.getCurrentSingleSignOnAccount(context);
            } else {
                this.ssoAccount = AccountImporter.getSingleSignOnAccount(context, ssoAccountName);
            }
        } catch (NextcloudFilesAppAccountNotFoundException | NoCurrentAccountSelectedException e) {
            DeckLog.logError(e);
        }
    }

    public DeckAPI getDeckAPI() {
        return deckAPI;
    }

    public NextcloudServerAPI getNextcloudAPI() {
        return nextcloudAPI;
    }

    public String getServerUrl(){
        if (ssoAccount == null) {
            setAccount();
        }
        return ssoAccount.url;
    }

    public String getApiPath() {
        return DECK_API_ENDPOINT;
    }

    public String getApiUrl() {
        return getServerUrl() + getApiPath();
    }
}
