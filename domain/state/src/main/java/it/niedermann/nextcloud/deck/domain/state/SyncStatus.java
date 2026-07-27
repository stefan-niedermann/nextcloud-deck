package it.niedermann.nextcloud.deck.domain.state;

import java.util.Collection;
import java.util.Objects;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;

public record SyncStatus(Account account,
                         Collection<Board> boardsInProgress,
                         long boardsTotalCount,
                         long boardsFinishedCount) {

    public SyncStatus {
        for (final var o : new Object[]{
                account,
                boardsInProgress,
        }) {
            Objects.requireNonNull(o);
        }
    }

    @Override
    public String toString() {
        return SyncStatus.class.getSimpleName() + " " + account.accountName() + ": " + boardsFinishedCount + " / " + boardsTotalCount;
    }
}
