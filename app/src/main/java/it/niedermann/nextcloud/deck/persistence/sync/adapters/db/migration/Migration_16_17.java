package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/573">Adds support for projects</a>
 */
public class Migration_16_17 extends Migration {

    public Migration_16_17() {
        super(16, 17);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE `OcsProject` (`localId` INTEGER PRIMARY KEY AUTOINCREMENT, `accountId` INTEGER NOT NULL, `id` INTEGER, `name` TEXT NOT NULL, `status` INTEGER NOT NULL, `lastModified` INTEGER, `lastModifiedLocal` INTEGER)");
        database.execSQL("CREATE UNIQUE INDEX `index_OcsProject_accountId_id` ON `OcsProject` (`accountId`, `id`)");
        database.execSQL("CREATE INDEX `index_project_accID` ON `OcsProject` (`accountId`)");
        database.execSQL("CREATE INDEX `index_OcsProject_id` ON `OcsProject` (`id`)");
        database.execSQL("CREATE INDEX `index_OcsProject_lastModifiedLocal` ON `OcsProject` (`lastModifiedLocal`)");

        database.execSQL("CREATE TABLE `OcsProjectResource` (`localId` INTEGER PRIMARY KEY AUTOINCREMENT, `accountId` INTEGER NOT NULL, `id` INTEGER, `name` TEXT, `status` INTEGER NOT NULL, `lastModified` INTEGER, `lastModifiedLocal` INTEGER, `projectId` INTEGER NOT NULL, `type` TEXT , `link` TEXT , `path` TEXT, `iconUrl` TEXT , `previewAvailable` INTEGER, `mimetype` TEXT, FOREIGN KEY(`projectId`) REFERENCES `OcsProject`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE)");
        database.execSQL("CREATE INDEX `index_projectResource_accID` ON `OcsProjectResource` (`accountId`)");
        database.execSQL("CREATE INDEX `index_projectResource_projectId` ON `OcsProjectResource` (`projectId`)");
        database.execSQL("CREATE UNIQUE INDEX `index_OcsProjectResource_accountId_id` ON `OcsProjectResource` (`accountId`, `id`, `projectId`)");
        database.execSQL("CREATE INDEX `index_OcsProjectResource_id` ON `OcsProjectResource` (`id`)");
        database.execSQL("CREATE INDEX `index_OcsProjectResource_lastModifiedLocal` ON `OcsProjectResource` (`lastModifiedLocal`)");

        database.execSQL("CREATE TABLE `JoinCardWithProject` (`status` INTEGER NOT NULL, `projectId` INTEGER NOT NULL, `cardId` INTEGER NOT NULL, PRIMARY KEY (`projectId`, `cardId`), FOREIGN KEY(`cardId`) REFERENCES `Card`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(`projectId`) REFERENCES `OcsProject`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE)");
        database.execSQL("CREATE INDEX `index_JoinCardWithProject_projectId` ON `JoinCardWithProject` (`projectId`)");
        database.execSQL("CREATE INDEX `index_JoinCardWithProject_cardId` ON `JoinCardWithProject` (`cardId`)");
    }
}
