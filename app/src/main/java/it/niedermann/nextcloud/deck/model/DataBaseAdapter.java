package it.niedermann.nextcloud.deck.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataBaseAdapter extends SQLiteOpenHelper {

    private static final String TABLE_ACCOUNTS = "ACCOUNTS";
    private static final String TABLE_BOARDS = "BOARDS";

    // ## SQLs
    // # creates:
    private static final String SQL_CREATE_ACCOUNTS_TABLE = "CREATE TABLE " + TABLE_ACCOUNTS +
            " ( " +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "ACCOUNT_NAME TEXT NOT NULL UNIQUE" +
            " )";
    private static final String SQL_CREATE_BOARDS_TABLE = "CREATE TABLE " + TABLE_BOARDS +
            "(" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "ACCOUNT_ID INTEGER NOT NULL, " +
            "BOARD_NAME TEXT NOT NULL UNIQUE, " +
            "FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNTS(ID)" +
            " )";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NEXTCLOUD_DECK";

    private static DataBaseAdapter INSTANCE;

    private Context context = null;

    private DataBaseAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
        //recreateDatabase(getWritableDatabase());
    }

    public static DataBaseAdapter getInstance(Context context) {
        if (INSTANCE == null)
            return INSTANCE = new DataBaseAdapter(context.getApplicationContext());
        else
            return INSTANCE;
    }

    /**
     * Creates initial the Database
     *
     * @param db Database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }

    private void createTables(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ACCOUNTS_TABLE);
        db.execSQL(SQL_CREATE_BOARDS_TABLE);
        createIndexes(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            recreateDatabase(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        recreateDatabase(db);
    }

    private void clearDatabase(SQLiteDatabase db) {
        db.delete(TABLE_BOARDS, null, null);
        db.delete(TABLE_ACCOUNTS, null, null);
    }

    private void recreateDatabase(SQLiteDatabase db) {
        dropIndexes(db);
        db.execSQL("DROP TABLE " + TABLE_BOARDS);
        db.execSQL("DROP TABLE " + TABLE_ACCOUNTS);
        onCreate(db);
    }

    private void dropIndexes(SQLiteDatabase db) {
        Cursor c = db.query("sqlite_master", new String[]{"name"}, "type=?", new String[]{"index"}, null, null, null);
        while (c.moveToNext()) {
            db.execSQL("DROP INDEX " + c.getString(0));
        }
        c.close();
    }

    private void createIndexes(SQLiteDatabase db) {
        // none so far...
    }

    private void createIndex(SQLiteDatabase db, String table, String column) {
        String indexName = table + "_" + column + "_idx";
        db.execSQL("CREATE INDEX IF NOT EXISTS " + indexName + " ON " + table + "(" + column + ")");
    }

    public Context getContext() {
        return context;
    }


    public Account createAccount(String accoutName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ACCOUNT_NAME", accoutName);
        long id = db.insert(TABLE_ACCOUNTS, null, values);
        return new Account(id, accoutName);
    }

    public void deleteAccount(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCOUNTS, "ID=?", new String[]{String.valueOf(id)});
    }

    public void updateAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("ACCOUNT_NAME", account.getName());
        db.update(TABLE_ACCOUNTS, values, "ID=?",
                new String[]{String.valueOf(account.getId())});
    }
    public Account readAccount(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNTS, new String[]{"ACCOUNT_NAME"}, "ID=?",
                new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        return new Account(id, cursor.getString(0));
    }
    public List<Account> readAccounts() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNTS, null, null,
                null, null, null, null);
        List<Account> accountList = new ArrayList<>();
        while (cursor.moveToNext()) {
            accountList.add(new Account(cursor.getLong(0), cursor.getString(1)));
        }
        return accountList;
    }

    public boolean hasAccounts() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ACCOUNTS, new String[]{"COUNT(ID)"}, null,
                null, null, null, null);
        List<Account> accountList = new ArrayList<>();
        cursor.moveToFirst();
        return cursor.getInt(0) > 0;
    }

    @NonNull
    @WorkerThread
    public Map<Long, Long> getIdMap() {
        Map<Long, Long> result = new HashMap<>();
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.query(table_notes, new String[]{key_remote_id, key_id}, key_status + " != ?", new String[]{DBStatus.LOCAL_DELETED.getTitle()}, null, null, null);
//        while (cursor.moveToNext()) {
//            result.put(cursor.getLong(0), cursor.getLong(1));
//        }
//        cursor.close();
        return result;
    }


    /**
     * Delete a single Note from the Database, if it has a specific DBStatus.
     * Thereby, an optimistic concurrency control is realized in order to prevent conflicts arising due to parallel changes from the UI and synchronization.
     *
     * @param id            long - ID of the Note that should be deleted.
     * @param forceDBStatus DBStatus, e.g., if Note was marked as LOCAL_DELETED (for DataBaseAdapter.SyncTask.pushLocalChanges()) or is unchanged VOID (for DataBaseAdapter.SyncTask.pullRemoteChanges())
     */
    //void deleteNote(long id, @NonNull DBStatus forceDBStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(table_notes,
//                key_id + " = ? AND " + key_status + " = ?",
     //           new String[]{String.valueOf(id), forceDBStatus.getTitle()});
    //}
}
