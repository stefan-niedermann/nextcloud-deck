package it.niedermann.nextcloud.deck.domain.model;

import java.util.Collection;

public record SyncStatus(Account account,
                         Collection<Board> boardsInProgress,
                         long boardsTotalCount,
                         long boardsFinishedCount) {

    @Override
    public String toString() {
        return SyncStatus.class.getSimpleName() + " " + account.accountName() + ": " + boardsFinishedCount + " / " + boardsTotalCount;
    }
}
