package it.niedermann.nextcloud.deck.feature_shared.util;

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
import java.util.logging.Logger;

@GlideModule
public class CustomAppGlideModule extends AppGlideModule {

    private static final Logger logger = Logger.getLogger(CustomAppGlideModule.class.getName());
    private static final ExecutorService clearDiskCacheExecutor = Executors.newSingleThreadExecutor();

    @Override
    public void registerComponents(@NonNull Context context,
                                   @NonNull Glide glide,
                                   @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
    }

    @UiThread
    public static void clearCache(@NonNull Context context) {
        final var cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            final var activeNetworkInfo = cm.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                logger.info("Clearing Glide memory cache");
                Glide.get(context).clearMemory();
                clearDiskCacheExecutor.submit(() -> {
                    logger.info("Clearing Glide disk cache");
                    Glide.get(context.getApplicationContext()).clearDiskCache();
                });
            } else {
                logger.info("Do not clear Glide caches, because the user currently does not have a working internet connection");
            }
        } else {
            logger.warning(ConnectivityManager.class.getSimpleName() + " is null");
        }
    }
}