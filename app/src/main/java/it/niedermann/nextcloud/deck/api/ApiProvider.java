package it.niedermann.nextcloud.deck.api;

import android.content.Context;

import com.nextcloud.android.sso.api.NextcloudAPI;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.exceptions.SSOException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import it.niedermann.nextcloud.deck.DeckLog;
import retrofit2.NextcloudRetrofitApiBuilder;

/**
 * Created by david on 26.05.17.
 */

public class ApiProvider {

    private static final String DECK_API_ENDPOINT = "/index.php/apps/deck/api/v1.0/";
    private static final String NC_API_ENDPOINT = "/ocs/v2.php/";

    private DeckAPI deckAPI;
    private NextcloudServerAPI nextcloudAPI;
    private Context context;
    private SingleSignOnAccount ssoAccount;

    public ApiProvider(Context context) {
        this.context = context;
    }

    public void initSsoApi(final NextcloudAPI.ApiConnectedListener callback) {
        try {
            setAccount();
            NextcloudAPI nextcloudAPI = new NextcloudAPI(context, ssoAccount, GsonConfig.getGson(), callback);
            deckAPI = new NextcloudRetrofitApiBuilder(nextcloudAPI, DECK_API_ENDPOINT).create(DeckAPI.class);
            this.nextcloudAPI = new NextcloudRetrofitApiBuilder(nextcloudAPI, NC_API_ENDPOINT).create(NextcloudServerAPI.class);
        } catch (SSOException e) {
            DeckLog.logError(e);
            callback.onError(e);
        }
    }

    private void setAccount() throws NextcloudFilesAppAccountNotFoundException, NoCurrentAccountSelectedException {
        ssoAccount = SingleAccountHelper.getCurrentSingleSignOnAccount(context);
    }

    public DeckAPI getDeckAPI() {
        return deckAPI;
    }

    public NextcloudServerAPI getNextcloudAPI() {
        return nextcloudAPI;
    }

    public String getServerUrl() throws NextcloudFilesAppAccountNotFoundException, NoCurrentAccountSelectedException {
        if (ssoAccount==null){
            setAccount();
        }
        return ssoAccount.url;
    }

    public String getApiPath(){
        return DECK_API_ENDPOINT;
    }

    public String getApiUrl() throws NextcloudFilesAppAccountNotFoundException, NoCurrentAccountSelectedException {
        return getServerUrl()+getApiPath();
    }
}
