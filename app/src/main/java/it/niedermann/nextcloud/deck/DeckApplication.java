package it.niedermann.nextcloud.deck;

import android.app.Application;
import android.os.StrictMode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import it.niedermann.nextcloud.deck.repository.PreferencesRepository;
import it.niedermann.nextcloud.deck.util.CustomAppGlideModule;

public class DeckApplication extends Application {

    private final ExecutorService executor = new ThreadPoolExecutor(0, 2, 0L, TimeUnit.SECONDS, new SynchronousQueue<>());

    @Override
    public void onCreate() {
        final var repo = new PreferencesRepository(this);

        if (BuildConfig.DEBUG) {
            enableStrictModeLogging();
        }

        repo.getAppThemeSetting().thenAcceptAsync(repo::setAppTheme, executor);
        repo.isDebugModeEnabled().thenAcceptAsync(DeckLog::enablePersistentLogs, executor);

        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        DeckLog.error("--- Low memory: Clear Glide cache ---");
        CustomAppGlideModule.clearCache(this);
        DeckLog.error("--- Low memory: Clear debug log ---");
        DeckLog.clearDebugLog();
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
