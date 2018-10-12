package it.niedermann.nextcloud.deck.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.nextcloud.android.sso.api.NextcloudAPI;
import com.nextcloud.android.sso.exceptions.SSOException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by david on 26.05.17.
 */

public class ApiProvider {

    private static final String TAG = ApiProvider.class.getCanonicalName();
    private DeckAPI mApi;
    private Context context;
    private boolean connected = false;
    NextcloudAPI nextcloudAPI = null;


    public ApiProvider(Context context) {
        this.context = context;
    }

    public void initSsoApi(final NextcloudAPI.ApiConnectedListener callback) {

        try {
            SingleSignOnAccount ssoAccount = SingleAccountHelper.getCurrentSingleSignOnAccount(context);
            nextcloudAPI = new NextcloudAPI(context, ssoAccount, GsonConfig.GetGson(), new NextcloudAPI.ApiConnectedListener() {
                @Override
                public void onConnected() {
                    connected = true;
                    mApi = new DeckAPI_SSO(nextcloudAPI);
                    callback.onConnected();
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                    callback.onError(ex);
                }
            });
        } catch (SSOException e) {
            callback.onError(e);
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public DeckAPI getAPI() {
        return mApi;
    }
}
