package it.niedermann.nextcloud.deck.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Flowable;

public class DataBaseAdapter {

    private static final Logger logger = Logger.getLogger(DataBaseAdapter.class.getName());
    private static volatile DataBaseAdapter instance;

    @NonNull
    private final DeckDatabase db;

    @VisibleForTesting
    protected DataBaseAdapter(@NonNull Context context) {
        this.db = DeckDatabase.getInstance(context);
    }

    private DataBaseAdapter(@NonNull DeckDatabase db) {
        this.db = db;
    }

    public static synchronized DataBaseAdapter getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (DeckDatabase.class) {
                if (instance == null) {
                    instance = new DataBaseAdapter(context);
                }
            }
        }
        return instance;
    }

    public Flowable<Boolean> hasAnyAccounts() {
        return db.getAccountDao().hasAccounts();
    }
}
