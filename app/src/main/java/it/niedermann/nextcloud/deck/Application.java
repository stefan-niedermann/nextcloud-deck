package it.niedermann.nextcloud.deck;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.ui.branding.Branded;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;
import static androidx.multidex.MultiDex.install;

public class Application extends android.app.Application {

    public static final long NO_ACCOUNT_ID = -1L;
    public static final long NO_BOARD_ID = -1L;
    public static final long NO_STACK_ID = -1L;

    private static boolean brandingEnabled;

    @NonNull
    private static List<Branded> brandedComponents = new ArrayList<>();

    @Override
    public void onCreate() {
        setAppTheme(getAppTheme(getApplicationContext()));

        brandingEnabled = getApplicationContext().getResources().getBoolean(R.bool.enable_brand);
        if (brandingEnabled) {
            @ColorInt final int mainColor = readBrandMainColor(getApplicationContext());
            @ColorInt final int textColor = readBrandTextColor(getApplicationContext());
            applyBrand(mainColor, textColor);
        }
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

    public static void setAppTheme(boolean darkTheme) {
        setDefaultNightMode(darkTheme ? MODE_NIGHT_YES : MODE_NIGHT_NO);
    }

    public static boolean getAppTheme(@NonNull Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_dark_theme), false);
    }

    // --------
    // Branding
    // --------

    public static void registerBrandedComponent(@NonNull Context context, @NonNull Branded brandedComponent) {
        if (brandingEnabled && !brandedComponents.contains(brandedComponent)) {
            brandedComponents.add(brandedComponent);

            @ColorInt final int mainColor = readBrandMainColor(context);
            @ColorInt final int textColor = readBrandTextColor(context);
            brandedComponent.applyBrand(mainColor, textColor);
        }
    }

    public static void deregisterBrandedComponent(@NonNull Branded brandedComponent) {
        brandedComponents.remove(brandedComponent);
    }

    public static void setBrand(@NonNull Context context, @ColorInt int mainColor, @ColorInt int textColor) {
        @ColorInt final int currentMainColor = readBrandMainColor(context);
        @ColorInt final int currentTextColor = readBrandTextColor(context);
        if (mainColor != currentMainColor || textColor != currentTextColor) {
            if (brandingEnabled) {
                applyBrand(mainColor, textColor);
            }
            saveBrandColors(context, mainColor, textColor);
        }
    }

    public static void applyBrand(@ColorInt int mainColor, @ColorInt int textColor) {
        for (Branded themableComponent : brandedComponents) {
            themableComponent.applyBrand(mainColor, textColor);
        }
    }

    @ColorInt
    public static int readBrandMainColor(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        DeckLog.log("--- Read: shared_preference_theme_main");
        return sharedPreferences.getInt(context.getString(R.string.shared_preference_theme_main), context.getApplicationContext().getResources().getColor(R.color.primary));
    }

    @ColorInt
    public static int readBrandTextColor(@NonNull Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        DeckLog.log("--- Read: shared_preference_theme_text");
        return sharedPreferences.getInt(context.getString(R.string.shared_preference_theme_text), context.getApplicationContext().getResources().getColor(android.R.color.white));
    }

    public static void saveBrandColors(@NonNull Context context, @ColorInt int mainColor, @ColorInt int textColor) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        DeckLog.log("--- Write: shared_preference_theme_main" + " | " + mainColor);
        DeckLog.log("--- Write: shared_preference_theme_text" + " | " + textColor);
        editor.putInt(context.getString(R.string.shared_preference_theme_main), mainColor);
        editor.putInt(context.getString(R.string.shared_preference_theme_text), textColor);
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
