package it.niedermann.nextcloud.deck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.multidex.MultiDexApplication;
import androidx.preference.PreferenceManager;

import it.niedermann.nextcloud.deck.ui.branding.BrandedActivity;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

public class DeckApplication extends MultiDexApplication {

    public static final long NO_ACCOUNT_ID = -1L;
    public static final long NO_BOARD_ID = -1L;
    public static final long NO_STACK_ID = -1L;

    @Override
    public void onCreate() {
        setAppTheme(isDarkTheme(getApplicationContext()));
        super.onCreate();
    }

    // -----------------
    // Day / Night theme
    // -----------------

    public static void setAppTheme(boolean darkTheme) {
        setDefaultNightMode(darkTheme ? MODE_NIGHT_YES : MODE_NIGHT_NO);
    }

    public static boolean isDarkTheme(@NonNull Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_dark_theme), false);
    }

    // --------
    // Branding
    // --------

    public static boolean isBrandingEnabled(@NonNull Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_branding), true);
    }

    @ColorInt
    public static int readBrandMainColor(@NonNull Context context) {
        if (isBrandingEnabled(context)) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            DeckLog.log("--- Read: shared_preference_theme_main");
            return sharedPreferences.getInt(context.getString(R.string.shared_preference_theme_main), context.getApplicationContext().getResources().getColor(R.color.primary));
        } else {
            return context.getResources().getColor(R.color.primary);
        }
    }

    public static void saveBrandColors(@NonNull Context context, @ColorInt int mainColor) {
        if (isBrandingEnabled(context) && context instanceof BrandedActivity) {
            final BrandedActivity activity = (BrandedActivity) context;
            activity.applyBrand(mainColor);
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: shared_preference_theme_main" + " | " + mainColor);
        editor.putInt(context.getString(R.string.shared_preference_theme_main), mainColor);
        editor.apply();
    }

    @SuppressLint("ApplySharedPref")
    public static void saveBrandColorsSynchronously(@NonNull Context context, @ColorInt int mainColor) {
        if (isBrandingEnabled(context) && context instanceof BrandedActivity) {
            final BrandedActivity activity = (BrandedActivity) context;
            activity.applyBrand(mainColor);
        }
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: shared_preference_theme_main" + " | " + mainColor);
        editor.putInt(context.getString(R.string.shared_preference_theme_main), mainColor);
        editor.commit();
    }

    // --------------------------------------
    // Current account / board / stack states
    // --------------------------------------

    @SuppressLint("ApplySharedPref")
    public static void saveCurrentAccountIdSynchronously(@NonNull Context context, long accountId) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: shared_preference_last_account" + " | " + accountId);
        editor.putLong(context.getString(R.string.shared_preference_last_account), accountId);
        editor.commit();
    }

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

    @SuppressLint("ApplySharedPref")
    public static void saveCurrentBoardIdSynchronously(@NonNull Context context, long accountId, long boardId) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: shared_preference_last_board_for_account_" + accountId + " | " + boardId);
        editor.putLong(context.getString(R.string.shared_preference_last_board_for_account_) + accountId, boardId);
        editor.commit();
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
