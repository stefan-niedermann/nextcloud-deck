package it.niedermann.nextcloud.deck.util.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.GlideUrl;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.aidl.NextcloudRequest;
import com.nextcloud.android.sso.api.NextcloudAPI;
import com.nextcloud.android.sso.api.Response;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.exceptions.TokenMismatchException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.GsonConfig;

import static it.niedermann.nextcloud.deck.util.glide.SingleSignOnOriginHeader.X_HEADER_SSO_ACCOUNT_NAME;


/**
 * Fetches an {@link InputStream} using the Nextcloud SSO library.
 */
public class SingleSignOnStreamFetcher implements DataFetcher<InputStream> {
    private static final String METHOD_GET = "GET";

    private static final Map<String, NextcloudAPI> INITIALIZED_APIs = new HashMap<>();

    private final Context context;
    private final GlideUrl url;

    // Public API.
    @SuppressWarnings("WeakerAccess")
    public SingleSignOnStreamFetcher(Context context, GlideUrl url) {
        this.context = context;
        this.url = url;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull final DataCallback<? super InputStream> callback) {
        NextcloudAPI client;
        try {
            final SingleSignOnAccount ssoAccount;
            if (url.getHeaders().containsKey(X_HEADER_SSO_ACCOUNT_NAME)) {
                ssoAccount = AccountImporter.getSingleSignOnAccount(context, url.getHeaders().get(X_HEADER_SSO_ACCOUNT_NAME));
            } else {
                ssoAccount = SingleAccountHelper.getCurrentSingleSignOnAccount(context);
            }
            client = INITIALIZED_APIs.get(ssoAccount.name);
            boolean didInitialize = false;
            if (client == null) {
                client = new NextcloudAPI(context, ssoAccount, GsonConfig.getGson(), new NextcloudAPI.ApiConnectedListener() {
                    @Override
                    public void onConnected() {
                        DeckLog.log("success: init SSO-Api");
                    }

                    @Override
                    public void onError(Exception e) {
                        DeckLog.logError(e);
                    }
                });
                INITIALIZED_APIs.put(ssoAccount.name, client);
                didInitialize = true;
            }

            NextcloudRequest.Builder requestBuilder;
            try {
                requestBuilder = new NextcloudRequest.Builder()
                        .setMethod(METHOD_GET)
                        .setUrl(url.toURL().getPath());
                Map<String, List<String>> header = new HashMap<>();
                for (Map.Entry<String, String> headerEntry : url.getHeaders().entrySet()) {
                    header.put(headerEntry.getKey(), Collections.singletonList(headerEntry.getValue()));
                }
                requestBuilder.setHeader(header);
                NextcloudRequest nextcloudRequest = requestBuilder.build();
                DeckLog.log(nextcloudRequest.toString());
                Response response = client.performNetworkRequestV2(nextcloudRequest);
                callback.onDataReady(response.getBody());
            } catch (MalformedURLException e) {
                callback.onLoadFailed(e);
            } catch (TokenMismatchException e) {
                if (!didInitialize) {
                    DeckLog.warn("SSO Glide loader failed with TokenMismatchException, trying to re-initialize...");
                    client.stop();
                    INITIALIZED_APIs.remove(ssoAccount.name);
                    loadData(priority, callback);
                } else {
                    DeckLog.logError(e);
                    callback.onLoadFailed(e);
                }
            } catch (Exception e) {
                callback.onLoadFailed(e);
            }

        } catch (NextcloudFilesAppAccountNotFoundException e) {
            e.printStackTrace();
        } catch (NoCurrentAccountSelectedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() {

    }

    @Override
    public void cancel() {

    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
