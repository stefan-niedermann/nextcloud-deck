package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Implement <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/597">Filter widget</a>
 */
public class Migration_25_26 extends Migration {

    public Migration_25_26() {
        super(25, 26);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE `FilterWidget` (`id` INTEGER PRIMARY KEY NOT NULL, `title` TEXT, `dueType` INTEGER, `widgetType` INTEGER NOT NULL)");
        database.execSQL("CREATE TABLE `FilterWidgetAccount` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `filterWidgetId` INTEGER, `accountId` INTEGER, `includeNoUser` INTEGER NOT NULL, `includeNoProject` INTEGER NOT NULL, FOREIGN KEY(`accountId`) REFERENCES `Account`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`filterWidgetId`) REFERENCES `FilterWidget`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        database.execSQL("CREATE TABLE `FilterWidgetBoard` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `filterAccountId` INTEGER, `boardId` INTEGER, `includeNoLabel` INTEGER NOT NULL, FOREIGN KEY(`boardId`) REFERENCES `Board`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`filterAccountId`) REFERENCES `FilterWidgetAccount`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        database.execSQL("CREATE TABLE `FilterWidgetLabel` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `filterBoardId` INTEGER, `labelId` INTEGER, FOREIGN KEY(`labelId`) REFERENCES `Label`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`filterBoardId`) REFERENCES `FilterWidgetBoard`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        database.execSQL("CREATE TABLE `FilterWidgetSort` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `filterWidgetId` INTEGER, `direction` INTEGER NOT NULL, `criteria` INTEGER NOT NULL, `ruleOrder` INTEGER NOT NULL, FOREIGN KEY(`filterWidgetId`) REFERENCES `FilterWidget`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        database.execSQL("CREATE TABLE `FilterWidgetStack` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `filterBoardId` INTEGER, `stackId` INTEGER, FOREIGN KEY(`stackId`) REFERENCES `Stack`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`filterBoardId`) REFERENCES `FilterWidgetBoard`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        database.execSQL("CREATE TABLE `FilterWidgetUser` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `filterAccountId` INTEGER, `userId` INTEGER, FOREIGN KEY(`userId`) REFERENCES `User`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`filterAccountId`) REFERENCES `FilterWidgetAccount`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        database.execSQL("CREATE TABLE `FilterWidgetProject` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `filterAccountId` INTEGER, `projectId` INTEGER, FOREIGN KEY(`projectId`) REFERENCES `OcsProject`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`filterAccountId`) REFERENCES `FilterWidgetAccount`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        database.execSQL("CREATE INDEX `index_FilterWidgetAccount_filterWidgetId` ON `FilterWidgetAccount` (`filterWidgetId`)");
        database.execSQL("CREATE INDEX `idx_FilterWidgetAccount_accountId` ON `FilterWidgetAccount` (`accountId`)");
        database.execSQL("CREATE INDEX `idx_FilterWidgetBoard_boardId` ON `FilterWidgetBoard` (`boardId`)");
        database.execSQL("CREATE INDEX `idx_FilterWidgetBoard_filterAccountId` ON `FilterWidgetBoard` (`filterAccountId`)");
        database.execSQL("CREATE INDEX `idx_FilterWidgetLabel_filterBoardId` ON `FilterWidgetLabel` (`filterBoardId`)");
        database.execSQL("CREATE INDEX `idx_FilterWidgetLabel_labelId` ON `FilterWidgetLabel` (`labelId`)");
        database.execSQL("CREATE INDEX `idx_FilterWidgetSort_filterWidgetId` ON `FilterWidgetSort` (`filterWidgetId`)");
        database.execSQL("CREATE INDEX `idx_FilterWidgetStack_filterBoardId` ON `FilterWidgetStack` (`filterBoardId`)");
        database.execSQL("CREATE INDEX `idx_FilterWidgetStack_stackId` ON `FilterWidgetStack` (`stackId`)");
        database.execSQL("CREATE INDEX `idx_FilterWidgetUser_filterAccountId` ON `FilterWidgetUser` (`filterAccountId`)");
        database.execSQL("CREATE INDEX `idx_FilterWidgetUser_userId` ON `FilterWidgetUser` (`userId`)");
        database.execSQL("CREATE INDEX `idx_FilterWidgetProject_filterAccountId` ON `FilterWidgetProject` (`filterAccountId`)");
        database.execSQL("CREATE INDEX `idx_FilterWidgetProject_projectId` ON `FilterWidgetProject` (`projectId`)");
        database.execSQL("CREATE INDEX `unique_idx_FilterWidgetSort_filterWidgetId_criteria` ON `FilterWidgetSort` (`filterWidgetId`, `criteria`)");
        database.execSQL("CREATE INDEX `unique_idx_FilterWidgetSort_filterWidgetId_ruleOrder` ON `FilterWidgetSort` (`filterWidgetId`, `ruleOrder`)");

    }
}
