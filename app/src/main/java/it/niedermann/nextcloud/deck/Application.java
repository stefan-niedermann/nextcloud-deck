package it.niedermann.nextcloud.deck;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;
import static androidx.multidex.MultiDex.install;

public class Application extends android.app.Application {

    public static final long NO_ACCOUNT_ID = -1L;
    public static final long NO_BOARD_ID = -1L;
    public static final long NO_STACK_ID = -1L;

    public interface NextcloudTheme {
        void applyNextcloudTheme(@ColorInt int mainColor, @ColorInt int textColor);
    }

    @NonNull
    private static List<NextcloudTheme> themableComponents = new ArrayList<>();

    @Override
    public void onCreate() {
        setAppTheme(getAppTheme(getApplicationContext()));

        @ColorInt final int mainColor = readNextcloudThemeMainColor(getApplicationContext());
        @ColorInt final int textColor = readNextcloudThemeTextColor(getApplicationContext());
        applyNextcloudTheme(mainColor, textColor);

        super.onCreate();
    }

    // --------
    // Multidex
    // --------

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        install(this);
    }

    // -----------------
    // Day / Night theme
    // -----------------

    public static void setAppTheme(Boolean darkTheme) {
        setDefaultNightMode(darkTheme ? MODE_NIGHT_YES : MODE_NIGHT_NO);
    }

    public static boolean getAppTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_dark_theme), false);
    }

    // -----------------
    // Nextcloud theming
    // -----------------

    public static void registerThemableComponent(@NonNull Context context, @NonNull NextcloudTheme themableComponent) {
        if (!themableComponents.contains(themableComponent)) {
            themableComponents.add(themableComponent);

            @ColorInt final int mainColor = readNextcloudThemeMainColor(context);
            @ColorInt final int textColor = readNextcloudThemeTextColor(context);
            themableComponent.applyNextcloudTheme(mainColor, textColor);
        }
    }

    public static void deregisterThemableComponent(@NonNull NextcloudTheme themableComponent) {
        if (themableComponents.contains(themableComponent)) {
            themableComponents.add(themableComponent);
        }
    }

    public static void setNextcloudTheme(@NonNull Context context, @ColorInt int mainColor, @ColorInt int textColor) {
        @ColorInt final int currentMainColor = readNextcloudThemeMainColor(context);
        @ColorInt final int currentTextColor = readNextcloudThemeTextColor(context);
        if (mainColor != currentMainColor || textColor != currentTextColor) {
            applyNextcloudTheme(mainColor, textColor);
            saveNextcloudThemeColors(context, mainColor, textColor);
        }
    }

    public static void applyNextcloudTheme(@ColorInt int mainColor, @ColorInt int textColor) {
        for (NextcloudTheme themableComponent : themableComponents) {
            themableComponent.applyNextcloudTheme(mainColor, textColor);
        }
    }

    @ColorInt
    public static int readNextcloudThemeMainColor(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        DeckLog.log("--- Read: shared_preference_theme_main");
        return sharedPreferences.getInt("shared_preference_theme_main", context.getApplicationContext().getResources().getColor(R.color.primary));
    }

    @ColorInt
    public static int readNextcloudThemeTextColor(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        DeckLog.log("--- Read: shared_preference_theme_text");
        return sharedPreferences.getInt("shared_preference_theme_text", context.getApplicationContext().getResources().getColor(android.R.color.white));
    }

    public static void saveNextcloudThemeColors(@NonNull Context context, @ColorInt int mainColor, @ColorInt int textColor) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: shared_preference_theme_main" + " | " + mainColor);
        DeckLog.log("--- Write: shared_preference_theme_text" + " | " + textColor);
        editor.putInt("shared_preference_theme_main", mainColor);
        editor.putInt("shared_preference_theme_text", textColor);
        editor.apply();
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
