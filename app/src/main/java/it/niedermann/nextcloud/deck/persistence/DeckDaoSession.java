package it.niedermann.nextcloud.deck.persistence;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import it.niedermann.nextcloud.deck.model.DaoMaster;
import it.niedermann.nextcloud.deck.model.DaoSession;

public class DeckDaoSession {

    private static final String DATABASE_NAME = "NC_DECK_DB";

    private static DeckDaoSession INSTANCE;

    private Context context = null;

    private DaoSession daoSession;

    private DeckDaoSession(Context context) {
        this.context = context;

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DATABASE_NAME);
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public static DeckDaoSession getInstance(Context context) {
        if (INSTANCE == null)
            return INSTANCE = new DeckDaoSession(context.getApplicationContext());
        else
            return INSTANCE;
    }

    public DaoSession session() {
        return daoSession;
    }
}
