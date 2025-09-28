package it.niedermann.nextcloud.deck;

import android.app.Application;
import android.os.StrictMode;

import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.feature.shared.util.CustomAppGlideModule;


public class DeckApplication extends Application {

    private static final Logger logger = Logger.getLogger(DeckApplication.class.getName());

    @Override
    public void onCreate() {

        if (BuildConfig.DEBUG) {
            enableStrictModeLogging();
        }

        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        logger.warning("--- Low memory: Clear Glide cache ---");
        CustomAppGlideModule.clearCache(this);
        logger.warning("--- Low memory: Clear debug log ---");
    }

    private void enableStrictModeLogging() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .permitDiskReads()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());
    }
}
