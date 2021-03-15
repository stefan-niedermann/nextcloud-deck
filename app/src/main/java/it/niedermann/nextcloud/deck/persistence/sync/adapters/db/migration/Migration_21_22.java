package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/715">Migrate from java.util.Date and java.util.Calendar to java.time.*</a>
 */
public class Migration_21_22 extends Migration {

    @NonNull
    private final Context context;

    public Migration_21_22(@NonNull Context context) {
        super(21, 22);
        this.context = context;
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        final SharedPreferences.Editor lastSyncPref = context.getApplicationContext().getSharedPreferences("it.niedermann.nextcloud.deck.last_sync", Context.MODE_PRIVATE).edit();
        final Cursor cursor = database.query("select id from `Account`");
        while (cursor.moveToNext()) {
            lastSyncPref.remove("lS_" + cursor.getLong(0));
        }
        cursor.close();
        lastSyncPref.apply();
    }
}
