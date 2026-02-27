package it.niedermann.nextcloud.deck.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Flowable;

public class DatabaseAdapter {

    private static final Logger logger = Logger.getLogger(DatabaseAdapter.class.getName());
    private static volatile DatabaseAdapter instance;

    @NonNull
    private final DeckDatabase db;

    @VisibleForTesting
    protected DatabaseAdapter(@NonNull Context context) {
        this.db = DeckDatabase.getInstance(context);
    }

    private DatabaseAdapter(@NonNull DeckDatabase db) {
        this.db = db;
    }

    public static synchronized DatabaseAdapter getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (DeckDatabase.class) {
                if (instance == null) {
                    instance = new DatabaseAdapter(context);
                }
            }
        }
        return instance;
    }

    public Flowable<Boolean> hasAnyAccounts() {
        return db.getAccountDao().hasAccounts();
    }
}
