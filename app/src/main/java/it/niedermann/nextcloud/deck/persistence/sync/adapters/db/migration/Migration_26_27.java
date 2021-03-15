package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import it.niedermann.nextcloud.deck.model.widget.filter.EWidgetType;

/**
 * @see <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/767">Migrate Stack Widget to Filter Widget infrastructure</a>
 */
public class Migration_26_27 extends Migration {

    public Migration_26_27() {
        super(26, 27);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {

        Cursor cursor = database.query("select s.localId, s.boardId, s.accountId, w.appWidgetId from `StackWidgetModel` w inner join `Stack` s on s.localId = w.stackId");
        while (cursor.moveToNext()) {
            Long localStackId = cursor.getLong(0);
            Long localBoardId = cursor.getLong(1);
            Long accountId = cursor.getLong(2);
            Long filterWidgetId = cursor.getLong(3);

            // widget
            ContentValues values = new ContentValues();
            values.put("widgetType", EWidgetType.STACK_WIDGET.getId());
            values.put("id", filterWidgetId);
            database.insert("FilterWidget", SQLiteDatabase.CONFLICT_NONE, values);

            // account
            values = new ContentValues();
            values.put("filterWidgetId", filterWidgetId);
            values.put("accountId", accountId);
            values.put("includeNoUser", false);
            values.put("includeNoProject", false);
            long filterWidgetAccountId = database.insert("FilterWidgetAccount", SQLiteDatabase.CONFLICT_NONE, values);

            // board
            values = new ContentValues();
            values.put("filterAccountId", filterWidgetAccountId);
            values.put("boardId", localBoardId);
            values.put("includeNoLabel", false);
            long filterWidgetBoardId = database.insert("FilterWidgetBoard", SQLiteDatabase.CONFLICT_NONE, values);

            // stack
            values = new ContentValues();
            values.put("filterBoardId", filterWidgetBoardId);
            values.put("stackId", localStackId);
            database.insert("FilterWidgetStack", SQLiteDatabase.CONFLICT_NONE, values);
        }

        // cleanup
        database.execSQL("DROP TABLE `StackWidgetModel`");
    }
}
