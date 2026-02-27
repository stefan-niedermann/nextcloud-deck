package it.niedermann.nextcloud.deck.feature.shared.util;

import android.content.Context;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.database.DeckDatabase;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import it.niedermann.nextcloud.deck.repository.sync.DefaultSyncScheduler;

public class Repositories {

    private static AccountRepository accountRepository;
    private static BoardRepository boardRepository;

    private Repositories() {
    }

    public static synchronized void init(@NonNull Context context) {
        final var databaseAdapter = DeckDatabase.getInstance(context.getApplicationContext());
        final var syncAdapter = DefaultSyncScheduler.getInstance(context.getApplicationContext());

        accountRepository = new AccountRepository(databaseAdapter, syncAdapter);
        boardRepository = new BoardRepository(databaseAdapter, syncAdapter);
    }

    public static AccountRepository getAccountRepository() {
        return accountRepository;
    }

    public static BoardRepository getBoardRepository() {
        return boardRepository;
    }
}
