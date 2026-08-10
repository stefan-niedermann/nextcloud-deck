package it.niedermann.nextcloud.deck.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Adds appearance customization columns to {@code FilterWidget} (title/background/text colors),
 * used by the widget configuration screen: https://github.com/stefan-niedermann/nextcloud-deck/issues/1792
 */
public class Migration_35_36 extends Migration {

    public Migration_35_36() {
        super(35, 36);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE `FilterWidget` ADD COLUMN `titleColor` INTEGER");
        database.execSQL("ALTER TABLE `FilterWidget` ADD COLUMN `backgroundColor` INTEGER");
        database.execSQL("ALTER TABLE `FilterWidget` ADD COLUMN `headerTextColor` INTEGER");
        database.execSQL("ALTER TABLE `FilterWidget` ADD COLUMN `listBackgroundColor` INTEGER");
        database.execSQL("ALTER TABLE `FilterWidget` ADD COLUMN `entryTextColor` INTEGER");
    }
}
