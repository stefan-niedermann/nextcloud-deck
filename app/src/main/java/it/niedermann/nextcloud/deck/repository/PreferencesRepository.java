package it.niedermann.nextcloud.deck.repository;

import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import it.niedermann.android.sharedpreferences.SharedPreferenceBooleanLiveData;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;

public class PreferencesRepository {

    private final ExecutorService executor;
    private final String PREF_KEY_THEME;
    private final String PREF_KEY_DEBUGGING;
    private final SharedPreferences sharedPreferences;
    private final Context context;

    public PreferencesRepository(@NonNull Context context) {
        this(context, new ThreadPoolExecutor(0, 2, 0L, TimeUnit.SECONDS, new SynchronousQueue<>()));
    }

    public PreferencesRepository(@NonNull Context context, @NonNull ExecutorService executor) {
        this.context = context.getApplicationContext();
        this.executor = executor;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        PREF_KEY_THEME = this.context.getString(R.string.pref_key_dark_theme);
        PREF_KEY_DEBUGGING = this.context.getString(R.string.pref_key_debugging);
    }

    // ---------
    // Debugging
    // ---------

    public CompletableFuture<Boolean> isDebugModeEnabled() {
        return supplyAsync(() -> {
            final boolean enabled = sharedPreferences.getBoolean(PREF_KEY_DEBUGGING, false);
            DeckLog.log("--- Read:", PREF_KEY_DEBUGGING, "→", enabled);
            return enabled;
        }, executor);
    }

    public LiveData<Boolean> isDebugModeEnabled$() {
        return new SharedPreferenceBooleanLiveData(sharedPreferences, PREF_KEY_DEBUGGING, false);
    }

    // -----
    // Theme
    // -----

    public void setAppTheme(int setting) {
        setDefaultNightMode(setting);
    }

    public CompletableFuture<Integer> getAppThemeSetting() {
        return supplyAsync(() -> {
            String mode;
            try {
                mode = sharedPreferences.getString(PREF_KEY_THEME, context.getString(R.string.pref_value_theme_system_default));
            } catch (ClassCastException e) {
                mode = sharedPreferences.getBoolean(PREF_KEY_THEME, false) ? context.getString(R.string.pref_value_theme_dark) : context.getString(R.string.pref_value_theme_light);
            }
            return Integer.parseInt(mode);
        }, executor);
    }
}
