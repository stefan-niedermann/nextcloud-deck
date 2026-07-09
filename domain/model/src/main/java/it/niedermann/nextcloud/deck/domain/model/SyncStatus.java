package it.niedermann.nextcloud.deck.domain.model;

import java.util.Collection;
import java.util.Objects;

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
