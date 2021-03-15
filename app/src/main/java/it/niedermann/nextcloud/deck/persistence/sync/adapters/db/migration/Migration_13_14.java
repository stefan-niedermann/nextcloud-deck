package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Adds support for comment responses
 */
public class Migration_13_14 extends Migration {

    public Migration_13_14() {
        super(13, 14);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE `DeckComment` ADD `parentId` INTEGER REFERENCES DeckComment(localId) ON DELETE CASCADE");
        database.execSQL("CREATE INDEX `idx_comment_parentID` ON DeckComment(parentId)");
    }
}
