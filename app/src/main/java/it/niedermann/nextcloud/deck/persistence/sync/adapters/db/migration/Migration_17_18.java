package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/435">Implement ETags for Capabilities endpoint</a>
 */
public class Migration_17_18 extends Migration {

    public Migration_17_18() {
        super(17, 18);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE `Account` ADD `etag` TEXT");
    }
}
