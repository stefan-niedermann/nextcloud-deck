package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Reset ETags for cards because <a href="https://github.com/nextcloud/deck/issues/2874">the attachments for this card might not be complete</a>.
 */
public class Migration_28_29 extends Migration {

    public Migration_28_29() {
        super(28, 29);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("UPDATE `Account` SET `boardsEtag` = NULL");
        database.execSQL("UPDATE `Board` SET `etag` = NULL");
        database.execSQL("UPDATE `Stack` SET `etag` = NULL");
        database.execSQL("UPDATE `Card` SET `etag` = NULL");
    }
}
