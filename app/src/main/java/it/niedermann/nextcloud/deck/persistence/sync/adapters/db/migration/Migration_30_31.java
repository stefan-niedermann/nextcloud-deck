package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://github.com/stefan-niedermann/nextcloud-deck/issues/923">Foreign keys don't cascade (Cards stay in the database after deleting an Account)</a>
 */
public class Migration_30_31 extends Migration {

    private static String[] TABLES = new String[] {"AccessControl", "Activity", "Attachment", "Board", "Card", "DeckComment", "Label", "OcsProject", "OcsProjectResource", "Stack", "User"};

    public Migration_30_31() {
        super(30, 31);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        for (String table : TABLES) {
            Cursor cursor = database.query("SELECT sql FROM sqlite_master WHERE type='table' AND name=?", new String[]{table});
            cursor.moveToNext();
            String originalCreateStatement = cursor.getString(0);
            List<String> indexCreates = new ArrayList<>();
            cursor = database.query("SELECT sql FROM SQLite_master WHERE type = 'index' AND tbl_name=?", new String[]{table});
            while (cursor.moveToNext()) {
                indexCreates.add(cursor.getString(0));
            }
            String newTableName = "`" + table + "_tmp`";
            String newCreate = "CREATE TABLE " + newTableName;
            String create = originalCreateStatement.replace("CREATE TABLE `" + table + "`", newCreate)
                    .replace("CREATE TABLE \"" + table + "\"", newCreate)
                    .replace("CREATE TABLE '" + table + "'", newCreate);
            create = create.substring(0, create.lastIndexOf(')'));
            create += ", FOREIGN KEY(`accountId`) REFERENCES `Account`(`id`) ON DELETE CASCADE )";
            if ("Board".equals(table)) {
                create = create.replace("SET NULL", "CASCADE");
            }

            // create copied table
            database.execSQL(create);
            // delete obsolete data from original one
            database.execSQL("DELETE FROM `"+table+"` where accountId not in (select id from `Account`)");
            // copy remaining rows
            database.execSQL("INSERT INTO "+newTableName+" select * from `"+table+"`");
            // remove old table
            database.execSQL("DROP TABLE `"+table+"`");
            // let copied table take place of original one
            database.execSQL("ALTER TABLE "+newTableName+" RENAME TO `"+table+"`");
            // recreate indices
            for (String indexCreate : indexCreates) {
                database.execSQL(indexCreate);
            }
        }

    }
}
