package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import it.niedermann.nextcloud.deck.persistence.sync.SyncWorker;

/**
 * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/570">Reinitializes the background synchronization</a> and
 * <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/525">cleans up old shared preferences</a>
 */
public class Migration_14_15 extends Migration {

    @NonNull
    private final Context context;

    public Migration_14_15(@NonNull Context context) {
        super(14, 15);
        this.context = context;
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        SyncWorker.update(context);
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .remove("it.niedermann.nextcloud.deck.theme_text")
                .apply();
    }
}
