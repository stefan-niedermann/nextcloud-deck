package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Adds support for new attachment types with Deck server <code>1.3.0</code>
 */
public class Migration_27_28 extends Migration {

    public Migration_27_28() {
        super(27, 28);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE `Attachment` ADD COLUMN `fileId` INTEGER");
    }
}
