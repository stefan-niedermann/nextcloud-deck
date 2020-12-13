package it.niedermann.nextcloud.deck;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;

import it.niedermann.nextcloud.deck.ui.settings.DarkModeSetting;

import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

public class DeckApplication extends MultiDexApplication {

    public static final long NO_ACCOUNT_ID = -1L;
    public static final long NO_BOARD_ID = -1L;
    public static final long NO_STACK_ID = -1L;

    private static String PREF_KEY_THEME;

    @Override
    public void onCreate() {
        PREF_KEY_THEME = getString(R.string.pref_key_dark_theme);
        setAppTheme(getAppTheme(getApplicationContext()));
        super.onCreate();
    }

    // -----------------
    // Day / Night theme
    // -----------------

    public static void setAppTheme(DarkModeSetting setting) {
        setDefaultNightMode(setting.getModeId());
    }

    public static DarkModeSetting getAppTheme(@NonNull Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String mode;
        try {
            mode = prefs.getString(PREF_KEY_THEME, DarkModeSetting.SYSTEM_DEFAULT.getPreferenceValue(context));
        } catch (ClassCastException e) {
            boolean darkModeEnabled = prefs.getBoolean(PREF_KEY_THEME, false);
            mode = darkModeEnabled ? DarkModeSetting.DARK.getPreferenceValue(context) : DarkModeSetting.LIGHT.getPreferenceValue(context);
        }
        return DarkModeSetting.fromPreferenceValue(context, mode);
    }

    public static boolean isDarkThemeActive(@NonNull Context context, DarkModeSetting setting) {
        if (setting == DarkModeSetting.SYSTEM_DEFAULT) {
            return isDarkThemeActive(context);
        } else {
            return setting == DarkModeSetting.DARK;
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

    public static void saveCurrentAccountId(@NonNull Context context, long accountId) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: shared_preference_last_account" + " | " + accountId);
        editor.putLong(context.getString(R.string.shared_preference_last_account), accountId);
        editor.apply();
    }

    public static long readCurrentAccountId(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long accountId = sharedPreferences.getLong(context.getString(R.string.shared_preference_last_account), NO_ACCOUNT_ID);
        DeckLog.log("--- Read: shared_preference_last_account" + " | " + accountId);
        return accountId;
    }

    public static void saveCurrentBoardId(@NonNull Context context, long accountId, long boardId) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: shared_preference_last_board_for_account_" + accountId + " | " + boardId);
        editor.putLong(context.getString(R.string.shared_preference_last_board_for_account_) + accountId, boardId);
        editor.apply();
    }

    public static long readCurrentBoardId(@NonNull Context context, long accountId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long boardId = sharedPreferences.getLong(context.getString(R.string.shared_preference_last_board_for_account_) + accountId, NO_BOARD_ID);
        DeckLog.log("--- Read: shared_preference_last_board_for_account_" + accountId + " | " + boardId);
        return boardId;
    }

    public static void saveCurrentStackId(@NonNull Context context, long accountId, long boardId, long stackId) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: shared_preference_last_stack_for_account_and_board_" + accountId + "_" + boardId + " | " + stackId);
        editor.putLong(context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId, stackId);
        editor.apply();
    }

    public static long readCurrentStackId(@NonNull Context context, long accountId, long boardId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long savedStackId = sharedPreferences.getLong(context.getString(R.string.shared_preference_last_stack_for_account_and_board_) + accountId + "_" + boardId, NO_STACK_ID);
        DeckLog.log("--- Read: shared_preference_last_stack_for_account_and_board" + accountId + "_" + boardId + " | " + savedStackId);
        return savedStackId;
    }
}
