package it.niedermann.nextcloud.deck;

import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;
import static androidx.lifecycle.Transformations.distinctUntilChanged;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;

import it.niedermann.android.sharedpreferences.SharedPreferenceIntLiveData;
import it.niedermann.nextcloud.deck.model.Account;

public class DeckApplication extends Application {

    public static final long NO_ACCOUNT_ID = -1L;
    public static final long NO_BOARD_ID = -1L;
    public static final long NO_STACK_ID = -1L;

    private static String PREF_KEY_THEME;
    private static String PREF_KEY_DEBUGGING;

    private static LiveData<Integer> currentAccountColor$;
    private static LiveData<Integer> currentBoardColor$;

    @Override
    public void onCreate() {
        PREF_KEY_THEME = getString(R.string.pref_key_dark_theme);
        PREF_KEY_DEBUGGING = getString(R.string.pref_key_debugging);
        setAppTheme(getAppTheme(this));
        DeckLog.enablePersistentLogs(isPersistentLoggingEnabled(this));
        final var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentAccountColor$ = distinctUntilChanged(new SharedPreferenceIntLiveData(sharedPreferences,
                getString(R.string.shared_preference_last_account_color),
                ContextCompat.getColor(this, R.color.defaultBrand)));
        currentBoardColor$ = distinctUntilChanged(new SharedPreferenceIntLiveData(sharedPreferences,
                getString(R.string.shared_preference_theme_main),
                ContextCompat.getColor(this, R.color.defaultBrand)));
        super.onCreate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        DeckLog.clearDebugLog();
        DeckLog.error("--- cleared log because of low memory ---");
    }

    // ---------
    // Debugging
    // ---------

    public static boolean isPersistentLoggingEnabled(@NonNull Context context) {
        final var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean enabled = sharedPreferences.getBoolean(PREF_KEY_DEBUGGING, false);
        DeckLog.log("--- Read:", PREF_KEY_DEBUGGING, "→", enabled);
        return enabled;
    }

    // -----------------
    // Day / Night theme
    // -----------------

    public static void setAppTheme(int setting) {
        setDefaultNightMode(setting);
    }

    public static int getAppTheme(@NonNull Context context) {
        final var prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String mode;
        try {
            mode = prefs.getString(PREF_KEY_THEME, context.getString(R.string.pref_value_theme_system_default));
        } catch (ClassCastException e) {
            mode = prefs.getBoolean(PREF_KEY_THEME, false) ? context.getString(R.string.pref_value_theme_dark) : context.getString(R.string.pref_value_theme_light);
        }
        return Integer.parseInt(mode);
    }

    public static boolean isDarkThemeActive(@NonNull Context context, int darkModeSetting) {
        return darkModeSetting == Integer.parseInt(context.getString(R.string.pref_value_theme_system_default))
                ? isDarkThemeActive(context)
                : darkModeSetting == Integer.parseInt(context.getString(R.string.pref_value_theme_dark));
    }

    public static boolean isDarkThemeActive(@NonNull Context context) {
        return (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public static boolean isDarkTheme(@NonNull Context context) {
        return isDarkThemeActive(context, getAppTheme(context));
    }

    // --------------------------------------
    // Current account / board / stack states
    // --------------------------------------

    public static void saveCurrentAccount(@NonNull Context context, @NonNull Account account) {
        final var editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write:", context.getString(R.string.shared_preference_last_account), "→", account.getId());
        editor.putLong(context.getString(R.string.shared_preference_last_account), account.getId());
        DeckLog.log("--- Write:", context.getString(R.string.shared_preference_last_account_color), "→", account.getColor());
        editor.putInt(context.getString(R.string.shared_preference_last_account_color), account.getColor());
        editor.apply();
    }

    public static LiveData<Integer> readCurrentAccountColor() {
        return currentAccountColor$;
    }

    public static LiveData<Integer> readCurrentBoardColor() {
        return currentBoardColor$;
    }

    @ColorInt
    public static int readCurrentAccountColor(@NonNull Context context) {
        final var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        @ColorInt final int accountColor = sharedPreferences.getInt(context.getString(R.string.shared_preference_last_account_color), context.getApplicationContext().getResources().getColor(R.color.defaultBrand));
        DeckLog.log("--- Read:", context.getString(R.string.shared_preference_last_account_color), "→", accountColor);
        return accountColor;
    }

    public static long readCurrentAccountId(@NonNull Context context) {
        final var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final long accountId = sharedPreferences.getLong(context.getString(R.string.shared_preference_last_account), NO_ACCOUNT_ID);
        DeckLog.log("--- Read:", context.getString(R.string.shared_preference_last_account), "→", accountId);
        return accountId;
    }

    public static void saveCurrentBoardId(@NonNull Context context, long accountId, long boardId) {
        final var editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write:", context.getString(R.string.shared_preference_last_board_for_account_) + accountId, "→", boardId);
        editor.putLong(context.getString(R.string.shared_preference_last_board_for_account_) + accountId, boardId);
        editor.apply();
    }

    public static long readCurrentBoardId(@NonNull Context context, long accountId) {
        final var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final long boardId = sharedPreferences.getLong(context.getString(R.string.shared_preference_last_board_for_account_) + accountId, NO_BOARD_ID);
        DeckLog.log("--- Read:", context.getString(R.string.shared_preference_last_board_for_account_) + accountId, "→", boardId);
        return boardId;
    }

    public static void saveCurrentStackId(@NonNull Context context, long accountId, long boardId, long stackId) {
        final var editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write:", context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId, "→", stackId);
        editor.putLong(context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId, stackId);
        editor.apply();
    }

    public static long readCurrentStackId(@NonNull Context context, long accountId, long boardId) {
        final var sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final long savedStackId = sharedPreferences.getLong(context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId, NO_STACK_ID);
        DeckLog.log("--- Read:", context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId, "→", savedStackId);
        return savedStackId;
    }
}
