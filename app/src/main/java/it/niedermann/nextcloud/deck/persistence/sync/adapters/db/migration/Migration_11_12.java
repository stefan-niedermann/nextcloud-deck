package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Adds support for the Single note widget
 */
public class Migration_11_12 extends Migration {

    public Migration_11_12() {
        super(11, 12);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE `SingleCardWidgetModel` (`widgetId` INTEGER PRIMARY KEY, `accountId` INTEGER, `boardId` INTEGER, `cardId` INTEGER, FOREIGN KEY(`accountId`) REFERENCES `Account`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(`boardId`) REFERENCES `Board`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(`cardId`) REFERENCES `Card`(`localId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        database.execSQL("CREATE INDEX `index_SingleCardWidgetModel_cardId` ON `SingleCardWidgetModel` (`cardId`)");
    }
}
