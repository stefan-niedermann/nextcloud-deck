package it.niedermann.nextcloud.deck;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;

import it.niedermann.android.sharedpreferences.SharedPreferenceIntLiveData;
import it.niedermann.nextcloud.deck.model.Account;

import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;
import static androidx.lifecycle.Transformations.distinctUntilChanged;

public class DeckApplication extends MultiDexApplication {

    public static final long NO_ACCOUNT_ID = -1L;
    public static final long NO_BOARD_ID = -1L;
    public static final long NO_STACK_ID = -1L;

    private static String PREF_KEY_THEME;
    private static String PREF_KEY_DEBUGGING;

    private static LiveData<Integer> currentAccountColor$;

    @Override
    public void onCreate() {
        PREF_KEY_THEME = getString(R.string.pref_key_dark_theme);
        PREF_KEY_DEBUGGING = getString(R.string.pref_key_debugging);
        setAppTheme(getAppTheme(this));
        DeckLog.enablePeristentLogs(isPersistentLoggingEnabled(this));
        currentAccountColor$ = distinctUntilChanged(new SharedPreferenceIntLiveData(PreferenceManager.getDefaultSharedPreferences(this),
                getString(R.string.shared_preference_last_account_color),
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
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enabled = sharedPreferences.getBoolean(PREF_KEY_DEBUGGING, false);
        DeckLog.log("--- Read:", PREF_KEY_DEBUGGING, "|", enabled);
        return enabled;
    }

    // -----------------
    // Day / Night theme
    // -----------------

    public static void setAppTheme(int setting) {
        setDefaultNightMode(setting);
    }

    public static int getAppTheme(@NonNull Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String mode;
        try {
            mode = prefs.getString(PREF_KEY_THEME, context.getString(R.string.pref_value_theme_system_default));
        } catch (ClassCastException e) {
            boolean darkModeEnabled = prefs.getBoolean(PREF_KEY_THEME, false);
            mode = darkModeEnabled ? context.getString(R.string.pref_value_theme_dark) : context.getString(R.string.pref_value_theme_light);
        }
        return Integer.parseInt(mode);
    }

    public static boolean isDarkThemeActive(@NonNull Context context, int darkModeSetting) {
        if (darkModeSetting == Integer.parseInt(context.getString(R.string.pref_value_theme_system_default))) {
            return isDarkThemeActive(context);
        } else {
            return darkModeSetting == Integer.parseInt(context.getString(R.string.pref_value_theme_dark));
        }
    }

    public static boolean isDarkThemeActive(@NonNull Context context) {
        int uiMode = context.getResources().getConfiguration().uiMode;
        return (uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public static boolean isDarkTheme(@NonNull Context context) {
        return isDarkThemeActive(context, getAppTheme(context));
    }

    // --------------------------------------
    // Current account / board / stack states
    // --------------------------------------

    public static void saveCurrentAccount(@NonNull Context context, @NonNull Account account) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: shared_preference_last_account |", account.getId());
        editor.putLong(context.getString(R.string.shared_preference_last_account), account.getId());
        DeckLog.log("--- Write: shared_preference_last_account_color | ", account.getColor());
        editor.putInt(context.getString(R.string.shared_preference_last_account_color), account.getColor());
        editor.apply();
    }

    public static LiveData<Integer> readCurrentAccountColor() {
        return currentAccountColor$;
    }

    @ColorInt
    public static int readCurrentAccountColor(@NonNull Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        DeckLog.log("--- Read: shared_preference_last_account_color");
        return sharedPreferences.getInt(context.getString(R.string.shared_preference_last_account_color), context.getApplicationContext().getResources().getColor(R.color.defaultBrand));
    }

    public static long readCurrentAccountId(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long accountId = sharedPreferences.getLong(context.getString(R.string.shared_preference_last_account), NO_ACCOUNT_ID);
        DeckLog.log("--- Read: shared_preference_last_account |", accountId);
        return accountId;
    }

    public static void saveCurrentBoardId(@NonNull Context context, long accountId, long boardId) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: shared_preference_last_board_for_account_" + accountId, "|", boardId);
        editor.putLong(context.getString(R.string.shared_preference_last_board_for_account_) + accountId, boardId);
        editor.apply();
    }

    public static long readCurrentBoardId(@NonNull Context context, long accountId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long boardId = sharedPreferences.getLong(context.getString(R.string.shared_preference_last_board_for_account_) + accountId, NO_BOARD_ID);
        DeckLog.log("--- Read: shared_preference_last_board_for_account_" + accountId, "|", boardId);
        return boardId;
    }

    public static void saveCurrentStackId(@NonNull Context context, long accountId, long boardId, long stackId) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: shared_preference_last_stack_for_account_and_board_" + accountId + "_" + boardId, "|", stackId);
        editor.putLong(context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId, stackId);
        editor.apply();
    }

    public static long readCurrentStackId(@NonNull Context context, long accountId, long boardId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long savedStackId = sharedPreferences.getLong(context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId, NO_STACK_ID);
        DeckLog.log("--- Read: shared_preference_last_stack_for_account_and_board" + accountId + "_" + boardId, "|", savedStackId);
        return savedStackId;
    }
}
