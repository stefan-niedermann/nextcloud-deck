package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration_18_19 extends Migration {

    public Migration_18_19() {
        super(18, 19);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        // https://github.com/stefan-niedermann/nextcloud-deck/issues/619
        database.execSQL("DROP INDEX `index_OcsProjectResource_accountId_id`");
        database.execSQL("ALTER TABLE `OcsProjectResource` ADD `idString` TEXT");
        database.execSQL("CREATE UNIQUE INDEX `index_OcsProjectResource_accountId_id` ON `OcsProjectResource` (`accountId`, `id`, `idString`, `projectId`)");
    }
}
