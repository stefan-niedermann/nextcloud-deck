package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Fixes issues with LDAP users when filtering
 * https://github.com/stefan-niedermann/nextcloud-deck/issues/492
 * https://github.com/stefan-niedermann/nextcloud-deck/issues/631
 */
public class Migration_19_20 extends Migration {

    public Migration_19_20() {
        super(19, 20);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE `UserInGroup` (`groupId` INTEGER NOT NULL, `memberId` INTEGER NOT NULL, " +
                "primary KEY(`groupId`, `memberId`), " +
                "FOREIGN KEY(`groupId`) REFERENCES `User`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                "FOREIGN KEY(`memberId`) REFERENCES `User`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE)");
        database.execSQL("CREATE UNIQUE INDEX `unique_idx_group_member` ON `UserInGroup` (`groupId`, `memberId`)");
        database.execSQL("CREATE INDEX `index_UserInGroup_groupId` ON `UserInGroup` (`groupId`)");
        database.execSQL("CREATE INDEX `index_UserInGroup_memberId` ON `UserInGroup` (`memberId`)");

        database.execSQL("CREATE TABLE `UserInBoard` (`userId` INTEGER NOT NULL, `boardId` INTEGER NOT NULL, " +
                "primary KEY(`userId`, `boardId`), " +
                "FOREIGN KEY(`userId`) REFERENCES `User`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                "FOREIGN KEY(`boardId`) REFERENCES `Board`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE)");
        database.execSQL("CREATE UNIQUE INDEX `unique_idx_user_board` ON `UserInBoard` (`userId`, `boardId`)");
        database.execSQL("CREATE INDEX `index_UserInBoard_userId` ON `UserInBoard` (`userId`)");
        database.execSQL("CREATE INDEX `index_UserInBoard_boardId` ON `UserInBoard` (`boardId`)");
    }
}
