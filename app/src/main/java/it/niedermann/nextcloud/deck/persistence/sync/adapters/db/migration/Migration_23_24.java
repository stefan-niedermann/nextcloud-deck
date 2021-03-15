package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import it.niedermann.nextcloud.deck.R;

/**
 * <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/392">Dark mode following system default</a>
 */
public class Migration_23_24 extends Migration {

    @NonNull
    private final Context context;

    public Migration_23_24(@NonNull Context context) {
        super(23, 24);
        this.context = context;
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String themePref = context.getString(R.string.pref_key_dark_theme);

        if (sharedPreferences.contains(themePref)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            final boolean darkTheme = sharedPreferences.getBoolean(themePref, false);
            editor.remove(themePref);
            editor.putString(themePref, darkTheme ? context.getString(R.string.pref_value_theme_dark) : context.getString(R.string.pref_value_theme_light));
            editor.apply();
        }
    }
}
