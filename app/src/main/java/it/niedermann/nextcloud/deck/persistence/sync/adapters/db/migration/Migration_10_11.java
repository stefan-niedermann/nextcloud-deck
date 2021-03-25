package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.migration;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import it.niedermann.nextcloud.deck.model.enums.DBStatus;

/**
 * Removes duplicate labels and ensures uniqueness
 */
public class Migration_10_11 extends Migration {

    public Migration_10_11() {
        super(10, 11);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {
        // replace duplicates with the server-known ones
        Cursor duplucatesCursor = database.query("SELECT boardId, title, count(*) FROM Label group by boardid, title having count(*) > 1");
        if (duplucatesCursor != null && duplucatesCursor.moveToFirst()) {
            do {
                long boardId = duplucatesCursor.getLong(0);
                String title = duplucatesCursor.getString(1);
                Cursor singleDuplicateCursor = database.query("select localId from Label where boardId = ? and title = ? order by id desc", new Object[]{boardId, title});
                if (singleDuplicateCursor != null && singleDuplicateCursor.moveToFirst()) {
                    long idToUse = -1;
                    do {
                        if (idToUse < 0) {
                            // desc order -> first one is the one with remote ID or a random one. keep this one.
                            idToUse = singleDuplicateCursor.getLong(0);
                            continue;
                        }
                        long idToReplace = singleDuplicateCursor.getLong(0);
                        Cursor cardsAssignedToDuplicateCursor = database.query("select cardId, exists(select 1 from JoinCardWithLabel ij where ij.labelId = ? and ij.cardId = cardId) " +
                                "from JoinCardWithLabel where labelId = ?", new Object[]{idToUse, idToReplace});
                        if (cardsAssignedToDuplicateCursor != null && cardsAssignedToDuplicateCursor.moveToFirst()) {
                            do {
                                long cardId = cardsAssignedToDuplicateCursor.getLong(0);
                                boolean hasDestinationLabelAssigned = cardsAssignedToDuplicateCursor.getInt(1) > 0;
                                database.execSQL("DELETE FROM JoinCardWithLabel where labelId = ? and cardId = ?", new Object[]{idToReplace, cardId});

                                if (!hasDestinationLabelAssigned) {
                                    database.execSQL("INSERT INTO JoinCardWithLabel (status,labelId,cardId) VALUES (?, ?, ?)", new Object[]{DBStatus.LOCAL_EDITED.getId(), idToUse, cardId});
                                }
                            } while (cardsAssignedToDuplicateCursor.moveToNext());
                        }
                        database.execSQL("DELETE FROM Label where localId = ?", new Object[]{idToReplace});
                    } while (singleDuplicateCursor.moveToNext());
                }
            } while (duplucatesCursor.moveToNext());
        }
        // database.execSQL("DELETE FROM Label WHERE id IS NULL AND EXISTS(SELECT 1 FROM Label il WHERE il.boardId = boardId AND il.title = title AND id IS NOT NULL)");
        database.execSQL("CREATE UNIQUE INDEX idx_label_title_unique ON Label(boardId, title)");
    }
}
