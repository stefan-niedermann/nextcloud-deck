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

    private static final String API_ENDPOINT = "/index.php/apps/deck/api/v1.0/";

    private DeckAPI mApi;
    private Context context;
    private SingleSignOnAccount ssoAccount;

    public ApiProvider(Context context) {
        this.context = context;
    }

    public void initSsoApi(final NextcloudAPI.ApiConnectedListener callback) {
        try {
            setAccount();
            NextcloudAPI nextcloudAPI = new NextcloudAPI(context, ssoAccount, GsonConfig.getGson(), callback);
            //mApi = new DeckAPI_SSO(nextcloudAPI);
            mApi = new NextcloudRetrofitApiBuilder(nextcloudAPI, API_ENDPOINT).create(DeckAPI.class);
        } catch (SSOException e) {
            DeckLog.logError(e);
            callback.onError(e);
        }
    }

    private void setAccount() throws NextcloudFilesAppAccountNotFoundException, NoCurrentAccountSelectedException {
        ssoAccount = SingleAccountHelper.getCurrentSingleSignOnAccount(context);
    }

    public DeckAPI getAPI() {
        return mApi;
    }

    public String getServerUrl() throws NextcloudFilesAppAccountNotFoundException, NoCurrentAccountSelectedException {
        if (ssoAccount==null){
            setAccount();
        }
        return ssoAccount.url;
    }

    public String getApiPath(){
        return API_ENDPOINT;
    }

    public String getApiUrl() throws NextcloudFilesAppAccountNotFoundException, NoCurrentAccountSelectedException {
        return getServerUrl()+getApiPath();
    }
}
