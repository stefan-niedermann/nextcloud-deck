package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Adds support for account colors, deck server versions and Nextcloud maintenance mode
 */
public class Migration_9_10 extends Migration {

    public Migration_9_10() {
        super(9, 10);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE `Account` ADD `color` TEXT NOT NULL DEFAULT '#0082c9'");
        database.execSQL("ALTER TABLE `Account` ADD `textColor` TEXT NOT NULL DEFAULT '#ffffff'");
        database.execSQL("ALTER TABLE `Account` ADD `serverDeckVersion` TEXT NOT NULL DEFAULT '0.6.4'");
        database.execSQL("ALTER TABLE `Account` ADD `maintenanceEnabled` INTEGER NOT NULL DEFAULT 0");
    }
}
