package it.niedermann.nextcloud.deck.database.migration;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Adds appearance configuration options to the Upcoming widget: https://github.com/stefan-niedermann/nextcloud-deck/issues/1792
 */
public class Migration_35_36 extends Migration {

    public Migration_35_36() {
        super(35, 36);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE `FilterWidget` add column titleColor INTEGER");
        database.execSQL("ALTER TABLE `FilterWidget` add column widgetBackgroundColor INTEGER");
        database.execSQL("ALTER TABLE `FilterWidget` add column sectionTextColor INTEGER");
        database.execSQL("ALTER TABLE `FilterWidget` add column listBackgroundColor INTEGER");
        database.execSQL("ALTER TABLE `FilterWidget` add column entryTextColor INTEGER");
    }
}
