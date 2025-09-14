package it.niedermann.nextcloud.deck.repository.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.gson.GsonBuilder;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import it.niedermann.nextcloud.deck.shared.model.Account;

@WorkerThread
public class OcsApiProvider<T> extends ApiProvider<T> {

    private static final String API_ENDPOINT_OCS = "/ocs/v2.php/";

    public OcsApiProvider(@NonNull Context context,
                          @NonNull Account account,
                          @NonNull Class<T> clazz) throws NextcloudFilesAppAccountNotFoundException {
        this(context, account, clazz, new GsonBuilder());
    }

    private OcsApiProvider(@NonNull Context context,
                           @NonNull Account account,
                           @NonNull Class<T> clazz,
                           @NonNull GsonBuilder gsonBuilder) throws NextcloudFilesAppAccountNotFoundException {
        super(context, account, clazz, gsonBuilder);
    }

    @Override
    @NonNull
    protected String getEndpoint() {
        return API_ENDPOINT_OCS;
    }
}
