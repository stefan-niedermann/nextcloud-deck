package it.niedermann.nextcloud.deck.deprecated.repository;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Context;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextcloud.android.sso.AccountImporter;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.ocs.Capabilities;
import it.niedermann.nextcloud.deck.remote.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import okhttp3.Headers;

public class CapabilitiesRepository extends AbstractRepository {

    public CapabilitiesRepository(@NonNull Context context) {
        super(context);
    }

    /// @return pair of [Capabilities] and the corresponding `eTag` header
    public CompletableFuture<Pair<Capabilities, String>> fetchCapabilitiesWithETag(@NonNull Account account, @Nullable String eTag) {
        return supplyAsync(() -> {
            final SingleSignOnAccount ssoAccount;
            try {
                ssoAccount = AccountImporter.getSingleSignOnAccount(context, account.getName());
            } catch (NextcloudFilesAppAccountNotFoundException e) {
                throw new CompletionException(e);
            }
            return new ServerAdapter(context, ssoAccount, connectivityUtil);
        })
                .thenComposeAsync(serverAdapter -> {
                    final var future = new CompletableFuture<Pair<Capabilities, String>>();
                    serverAdapter.getCapabilities(eTag, ResponseCallback.from(account, new IResponseCallback<>() {

                        @Override
                        public void onResponse(Capabilities response, Headers headers) {
                            future.complete(new Pair<>(response, headers.get("ETag")));
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            IResponseCallback.super.onError(throwable);
                            future.completeExceptionally(throwable);
                        }
                    }));
                    return future;
                });

    }
}
