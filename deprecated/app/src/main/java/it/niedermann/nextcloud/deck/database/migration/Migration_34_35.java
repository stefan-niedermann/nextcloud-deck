package it.niedermann.nextcloud.deck.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Adds support for marking a card as done: https://github.com/stefan-niedermann/nextcloud-deck/issues/1556
 */
public class Migration_34_35 extends Migration {

    public Migration_34_35() {
        super(34, 35);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE `Card` add column startDate INTEGER");
        database.execSQL("ALTER TABLE `Card` add column color INTEGER");
        // Reset ETags: Refetch all cards to support startdate which did not change ETags
        database.execSQL("UPDATE `Account` SET `boardsEtag` = NULL");
        database.execSQL("UPDATE `Board` SET `etag` = NULL");
        database.execSQL("UPDATE `Stack` SET `etag` = NULL");
        database.execSQL("UPDATE `Card` SET `etag` = NULL");

        database.execSQL("create table `DependentCards` (" +
                "`localCardId` INTEGER NOT NULL, " +
                "`dependentRemoteCardId` INTEGER NOT NULL, " +
                "`status` INTEGER NOT NULL, " +
                "PRIMARY KEY(`localCardId`, `dependentRemoteCardId`), " +
                "FOREIGN KEY(`localCardId`) REFERENCES `Card`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE " +
            ")");
        database.execSQL("CREATE INDEX `index_localCardId` ON `DependentCards` (`localCardId`)");
        database.execSQL("CREATE INDEX `index_dependantRemoteCrardId` ON `DependentCards` (`dependentRemoteCardId`)");
    }
}
