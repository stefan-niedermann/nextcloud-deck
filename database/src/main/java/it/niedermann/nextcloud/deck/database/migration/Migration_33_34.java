package it.niedermann.nextcloud.deck.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Adds support for marking a card as done: https://github.com/stefan-niedermann/nextcloud-deck/issues/1556
 */
public class Migration_33_34 extends Migration {

    public Migration_33_34() {
        super(33, 34);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE `User` add column type INTEGER not null default 0");
        // Reset ETags to refetch Users
        database.execSQL("UPDATE `Account` SET `boardsEtag` = NULL");
    }
}
