package it.niedermann.nextcloud.deck.database.migration;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 */
public class Migration_31_32 extends Migration {

    @NonNull
    private final Context context;
    public Migration_31_32(@NonNull Context context) {
        super(31, 32);
        this.context = context;
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .remove("it.niedermann.nextcloud.deck.theme_main")
                .remove("it.niedermann.nextcloud.deck.last_account_color")
                .apply();
    }
}
