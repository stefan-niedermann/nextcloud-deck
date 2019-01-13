package it.niedermann.nextcloud.deck.api;

import android.content.Context;

import com.nextcloud.android.sso.api.NextcloudAPI;
import com.nextcloud.android.sso.exceptions.SSOException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import retrofit2.NextcloudRetrofitApiBuilder;

/**
 * Created by david on 26.05.17.
 */

public class ApiProvider {

    private static final String TAG = ApiProvider.class.getCanonicalName();
    private static final String API_ENDPOINT = "/index.php/apps/deck/api/v1.0/";

    private DeckAPI mApi;
    private Context context;

    public ApiProvider(Context context) {
        this.context = context;
    }

    void initSsoApi(final NextcloudAPI.ApiConnectedListener callback) {
        try {
            SingleSignOnAccount ssoAccount = SingleAccountHelper.getCurrentSingleSignOnAccount(context);
            NextcloudAPI nextcloudAPI = new NextcloudAPI(context, ssoAccount, GsonConfig.GetGson(), callback);
            //mApi = new DeckAPI_SSO(nextcloudAPI);
            mApi = new NextcloudRetrofitApiBuilder(nextcloudAPI, API_ENDPOINT).create(DeckAPI.class);
        } catch (SSOException e) {
            callback.onError(e);
        }
    }

    public DeckAPI getAPI() {
        return mApi;
    }
}
