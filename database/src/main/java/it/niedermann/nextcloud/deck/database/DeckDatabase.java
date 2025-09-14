package it.niedermann.nextcloud.deck.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.database.dao.AccountDao;
import it.niedermann.nextcloud.deck.database.entity.AccountEntity;

@Database(
        entities = {
                AccountEntity.class,
        },
        exportSchema = false,
        version = 34
)
@TypeConverters()
public abstract class DeckDatabase extends RoomDatabase {

    private static final Logger logger = Logger.getLogger(DeckDatabase.class.getName());
    private static final String DECK_DB_NAME = "NC_DECK_DB.db";
    private static volatile DeckDatabase instance;

    public static final RoomDatabase.Callback ON_CREATE_CALLBACK = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            logger.info("Database " + DECK_DB_NAME + " created.");
        }
    };

    public static synchronized DeckDatabase getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (DeckDatabase.class) {
                if (instance == null) {
                    instance = create(context);
                }
            }
        }
        return instance;
    }

    private static DeckDatabase create(final Context context) {
        return Room.databaseBuilder(
                        context,
                        DeckDatabase.class,
                        DECK_DB_NAME)
                .fallbackToDestructiveMigration(true)
                .addCallback(ON_CREATE_CALLBACK)
                .build();
    }

    public abstract AccountDao getAccountDao();
}