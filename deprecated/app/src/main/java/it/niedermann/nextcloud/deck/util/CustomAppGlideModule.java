package it.niedermann.nextcloud.deck.util;

import android.content.Context;
import android.net.ConnectivityManager;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.niedermann.nextcloud.deck.DeckLog;

@GlideModule
public class CustomAppGlideModule extends AppGlideModule {

    private static final ExecutorService clearDiskCacheExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
    }

    @UiThread
    public static void clearCache(@NonNull Context context) {
        final var cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            final var activeNetworkInfo = cm.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                DeckLog.info("Clearing Glide memory cache");
                Glide.get(context).clearMemory();
                clearDiskCacheExecutor.submit(() -> {
                    DeckLog.info("Clearing Glide disk cache");
                    Glide.get(context.getApplicationContext()).clearDiskCache();
                });
            } else {
                DeckLog.info("Do not clear Glide caches, because the user currently does not have a working internet connection");
            }
        } else {
            DeckLog.warn(ConnectivityManager.class.getSimpleName(), "is null");
        }
    }
}