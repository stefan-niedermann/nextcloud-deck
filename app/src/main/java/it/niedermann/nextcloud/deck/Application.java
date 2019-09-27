package it.niedermann.nextcloud.deck;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        setAppTheme(getAppTheme(getApplicationContext()));
        super.onCreate();
    }

    public static void setAppTheme(Boolean darkTheme) {
        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static boolean getAppTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_key_dark_theme), false);
    }
}
