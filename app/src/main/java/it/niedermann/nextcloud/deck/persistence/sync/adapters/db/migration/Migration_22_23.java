package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/359">Implement ETags for synchronization Speed-Up</a>
 */
public class Migration_22_23 extends Migration {

    public Migration_22_23() {
        super(22, 23);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE `Account` ADD `boardsEtag` TEXT");
        database.execSQL("ALTER TABLE `Board` ADD `etag` TEXT");
        database.execSQL("ALTER TABLE `Stack` ADD `etag` TEXT");
        database.execSQL("ALTER TABLE `Card` ADD `etag` TEXT");
        database.execSQL("ALTER TABLE `Label` ADD `etag` TEXT");
        database.execSQL("ALTER TABLE `AccessControl` ADD `etag` TEXT");
        database.execSQL("ALTER TABLE `Attachment` ADD `etag` TEXT");
        database.execSQL("ALTER TABLE `User` ADD `etag` TEXT");
        database.execSQL("ALTER TABLE `DeckComment` ADD `etag` TEXT");
        database.execSQL("ALTER TABLE `Activity` ADD `etag` TEXT");
        database.execSQL("ALTER TABLE `OcsProject` ADD `etag` TEXT");
        database.execSQL("ALTER TABLE `OcsProjectResource` ADD `etag` TEXT");
    }
}
