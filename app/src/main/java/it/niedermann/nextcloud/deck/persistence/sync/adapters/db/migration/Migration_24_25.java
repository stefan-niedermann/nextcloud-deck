package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Reset ETags (comments weren't loading due to bug)
 */
public class Migration_24_25 extends Migration {

    public Migration_24_25() {
        super(24, 25);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("UPDATE `Account` SET `boardsEtag` = NULL");
        database.execSQL("UPDATE `Board` SET `etag` = NULL");
        database.execSQL("UPDATE `Stack` SET `etag` = NULL");
        database.execSQL("UPDATE `Card` SET `etag` = NULL");
    }
}
