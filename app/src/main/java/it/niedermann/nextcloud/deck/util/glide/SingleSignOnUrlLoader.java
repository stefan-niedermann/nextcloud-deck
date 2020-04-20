package it.niedermann.nextcloud.deck.util.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.nextcloud.android.sso.api.NextcloudAPI;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException;
import com.nextcloud.android.sso.helper.SingleAccountHelper;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.GsonConfig;

/**
 * A simple model loader for fetching media over http/https using Nextcloud SSO.
 */
public class SingleSignOnUrlLoader implements ModelLoader<GlideUrl, InputStream> {

    private final NextcloudAPI client;

    // Public API.
    @SuppressWarnings("WeakerAccess")
    public SingleSignOnUrlLoader(@NonNull NextcloudAPI client) {
        this.client = client;
    }

    @Override
    public boolean handles(@NonNull GlideUrl url) {
        return true;
    }

    @Override
    public LoadData<InputStream> buildLoadData(
            @NonNull GlideUrl model, int width, int height, @NonNull Options options) {
        return new LoadData<>(model, new SingleSignOnStreamFetcher(client, model));
    }

    /**
     * The default factory for {@link SingleSignOnUrlLoader}s.
     */
    // Public API.
    @SuppressWarnings("WeakerAccess")
    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private SingleSignOnUrlLoader loader;

        /**
         * Constructor for a new Factory that runs requests using given client.
         */
        public Factory(@NonNull Context context) {
            try {
                loader = new SingleSignOnUrlLoader(new NextcloudAPI(context, SingleAccountHelper.getCurrentSingleSignOnAccount(context), GsonConfig.getGson(), new NextcloudAPI.ApiConnectedListener() {
                    @Override
                    public void onConnected() {
                        DeckLog.log("success: init SSO-Api");
                    }

                    @Override
                    public void onError(Exception e) {
                        DeckLog.logError(e);
                    }
                }));
            } catch (NextcloudFilesAppAccountNotFoundException e) {
                e.printStackTrace();
            } catch (NoCurrentAccountSelectedException e) {
                e.printStackTrace();
            }
        }

        @NonNull
        @Override
        public ModelLoader<GlideUrl, InputStream> build(@NotNull MultiModelLoaderFactory multiFactory) {
            return loader;
        }

        @Override
        public void teardown() {
            // Do nothing, this instance doesn't own the client.
        }
    }
}
