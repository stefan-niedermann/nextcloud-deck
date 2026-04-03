package it.niedermann.nextcloud.deck.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Adds support for Start-dates of cards: https://github.com/nextcloud/deck/pull/7749
 */
public class Migration_34_35 extends Migration {

    public Migration_34_35() {
        super(34, 35);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE `Card` add column startDate INTEGER");
        // Reset ETags to refetch Cards
        database.execSQL("UPDATE `Account` SET `boardsEtag` = NULL");
        database.execSQL("UPDATE `Board` SET `etag` = NULL");
        database.execSQL("UPDATE `Stack` SET `etag` = NULL");
        database.execSQL("UPDATE `Card` SET `etag` = NULL");
    }
}
