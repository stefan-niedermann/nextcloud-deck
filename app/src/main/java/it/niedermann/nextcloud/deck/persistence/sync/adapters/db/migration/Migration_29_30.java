package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/392">Dark mode following system default</a>
 */
public class Migration_29_30 extends Migration {

    @NonNull
    private final Context context;

    public Migration_29_30(@NonNull Context context) {
        super(29, 30);
        this.context = context;
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .remove("branding")
                .apply();
    }
}
