package it.niedermann.nextcloud.deck.repository.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.gson.GsonBuilder;
import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.api.NextcloudAPI;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.remote.ocs.OcsAPI;
import it.niedermann.nextcloud.deck.shared.model.Account;
import retrofit2.NextcloudRetrofitApiBuilder;

@WorkerThread
public abstract class ApiProvider<T> implements AutoCloseable {

    private static final Logger logger = Logger.getLogger(ApiProvider.class.getSimpleName());

    protected final NextcloudAPI nextcloudAPI;
    protected final T api;

    protected ApiProvider(@NonNull Context context,
                          @NonNull Account account,
                          @NonNull Class<T> clazz,
                          @NonNull GsonBuilder gsonBuilder) throws NextcloudFilesAppAccountNotFoundException {
        this.nextcloudAPI = new NextcloudAPI(
                context,
                AccountImporter.getSingleSignOnAccount(context, account.getAccountName()),
                gsonBuilder.create(),
                e -> logger.log(Level.SEVERE, e.toString(), e));

        this.api = new NextcloudRetrofitApiBuilder(nextcloudAPI, getEndpoint()).create(clazz);
    }

    @NonNull
    protected abstract String getEndpoint();

    public static ApiProvider<OcsAPI> getOcsApiProvider(@NonNull Context context,
                                                        @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        return new OcsApiProvider<>(context, account, OcsAPI.class);
    }

    public T getApi() {
        return this.api;
    }

    @Override
    public void close() {
        this.nextcloudAPI.close();
    }
}
