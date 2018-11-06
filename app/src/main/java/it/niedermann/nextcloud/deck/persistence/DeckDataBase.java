package it.niedermann.nextcloud.deck.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;

import static it.niedermann.nextcloud.deck.persistence.sql.DataBaseConsts.ALL_CREATES;
import static it.niedermann.nextcloud.deck.persistence.sql.DataBaseConsts.ALL_CREATE_INDICES;
import static it.niedermann.nextcloud.deck.persistence.sql.DataBaseConsts.ALL_TABLES;
import static it.niedermann.nextcloud.deck.persistence.sql.DataBaseConsts.TABLE_ACCOUNTS;

public class DeckDataBase extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NEXTCLOUD_DECK";

    private static DeckDataBase INSTANCE;

    private Context context = null;

    private DeckDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
        //recreateDatabase(getWritableDatabase());
    }

    public static DeckDataBase getInstance(Context context) {
        if (INSTANCE == null)
            return INSTANCE = new DeckDataBase(context.getApplicationContext());
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
        for (String create: ALL_CREATES){
            db.execSQL(create);
        }
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
        for (String table: ALL_TABLES){
            db.delete(table, null, null);
        }
    }

    private void recreateDatabase(SQLiteDatabase db) {
        dropIndexes(db);
        for (String table: ALL_TABLES){
            db.execSQL("DROP TABLE IF EXISTS " + table);
        }

        onCreate(db);
    }

    private void dropIndexes(SQLiteDatabase db) {
        Cursor c = db.query("sqlite_master", new String[]{"name"}, "type=?", new String[]{"index"}, null, null, null);
        while (c.moveToNext()) {
            db.execSQL("DROP INDEX IF EXISTS " + c.getString(0));
        }
        c.close();
    }

    private void createIndexes(SQLiteDatabase db) {
        for (String create: ALL_CREATE_INDICES){
            db.execSQL(create);
        }
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
}
