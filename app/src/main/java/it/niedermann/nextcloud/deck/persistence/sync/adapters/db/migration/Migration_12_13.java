package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration_12_13 extends Migration {

    public Migration_12_13() {
        super(12, 13);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("CREATE INDEX `idx_cardWidgetModel_accountId` ON `SingleCardWidgetModel` (`accountId`)");
        database.execSQL("CREATE INDEX `idx_cardWidgetModel_boardId` ON `SingleCardWidgetModel` (`boardId`)");
    }
}
