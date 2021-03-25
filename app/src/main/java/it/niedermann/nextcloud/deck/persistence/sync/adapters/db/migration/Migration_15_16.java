package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Adds support for Stack widget
 */
public class Migration_15_16 extends Migration {

    public Migration_15_16() {
        super(15, 16);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE `StackWidgetModel` (`appWidgetId` INTEGER PRIMARY KEY, `accountId` INTEGER, `stackId` INTEGER, `darkTheme` INTEGER CHECK (`darkTheme` IN (0,1)) NOT NULL, " +
                "FOREIGN KEY(`accountId`) REFERENCES `Account`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, " +
                "FOREIGN KEY(`stackId`) REFERENCES `Stack`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        database.execSQL("CREATE INDEX `index_StackWidgetModel_stackId` ON `StackWidgetModel` (`stackId`)");
        database.execSQL("CREATE INDEX `index_StackWidgetModel_accountId` ON `StackWidgetModel` (`accountId`)");
    }
}
