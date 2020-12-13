package it.niedermann.nextcloud.deck.ui.settings;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.NoSuchElementException;

import it.niedermann.nextcloud.deck.R;

/**
 * Possible values of the Dark Mode Setting.
 * <p>
 * The Dark Mode Setting can be stored in {@link android.content.SharedPreferences} as String by using {@link DarkModeSetting#getPreferenceValue(Context)} and received via {@link DarkModeSetting#fromPreferenceValue(Context, String)}.
 * <p>
 * Additionally, the equivalent {@link AppCompatDelegate}-Mode can be received via {@link #getModeId()}.
 *
 * @see AppCompatDelegate#MODE_NIGHT_YES
 * @see AppCompatDelegate#MODE_NIGHT_NO
 * @see AppCompatDelegate#MODE_NIGHT_FOLLOW_SYSTEM
 */
public enum DarkModeSetting {

    /**
     * Always use light mode.
     */
    LIGHT(AppCompatDelegate.MODE_NIGHT_NO, R.string.pref_value_theme_light),
    /**
     * Always use dark mode.
     */
    DARK(AppCompatDelegate.MODE_NIGHT_YES, R.string.pref_value_theme_dark),
    /**
     * Follow the global system setting for dark mode.
     */
    SYSTEM_DEFAULT(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, R.string.pref_value_theme_system_default);

    private final int modeId;
    private final @StringRes int preferenceValue;

    DarkModeSetting(int modeId, @StringRes int preferenceValue) {
        this.modeId = modeId;
        this.preferenceValue = preferenceValue;
    }

    public int getModeId() {
        return modeId;
    }

    public String getPreferenceValue(@NonNull Context context) {
        return context.getString(preferenceValue);
    }

    /**
     * Returns the instance of {@link DarkModeSetting} that corresponds to the preferenceValue
     *
     * @param preferenceValue String that is stored in shared preferences
     * @return An instance of {@link DarkModeSetting}
     */
    public static DarkModeSetting fromPreferenceValue(@NonNull Context context, String preferenceValue) {
        for (DarkModeSetting value : DarkModeSetting.values()) {
            if (context.getString(value.preferenceValue).equals(preferenceValue)) {
                return value;
            }
        }

        throw new NoSuchElementException("No NightMode with preferenceValue \"" + preferenceValue + "\" found");
    }
}
