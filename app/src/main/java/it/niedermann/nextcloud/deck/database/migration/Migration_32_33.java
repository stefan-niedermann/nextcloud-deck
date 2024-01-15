package it.niedermann.nextcloud.deck.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Adds support for marking a card as done: https://github.com/stefan-niedermann/nextcloud-deck/issues/1556
 */
public class Migration_32_33 extends Migration {

    public Migration_32_33() {
        super(32, 33);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE `Card` add column done INTEGER");
        // reset etags, so cards will be fetched after app-update
        database.execSQL("UPDATE `Account` SET `boardsEtag` = NULL");
        database.execSQL("UPDATE `Board` SET `etag` = NULL");
        database.execSQL("UPDATE `Stack` SET `etag` = NULL");
        database.execSQL("UPDATE `Card` SET `etag` = NULL");
    }
}
