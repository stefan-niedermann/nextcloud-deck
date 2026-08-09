package it.niedermann.nextcloud.deck.domain.state;

import java.util.Objects;
import it.niedermann.nextcloud.deck.domain.model.Account;

public record SyncStatus(
    Account account,
    long boardsTotal,
    long boardsFinished,
    String currentBoardTitle,
    long columnsTotal,
    long columnsFinished,
    String currentColumnTitle,
    long cardsTotal,
    long cardsFinished
) {
    public SyncStatus(Account account) {
        this(account, 0, 0, null, 0, 0, null, 0, 0);
    }

    public SyncStatus {
        Objects.requireNonNull(account);
    }

    public SyncStatus withBoards(long total, long finished, String currentTitle) {
        return new SyncStatus(account, total, finished, currentTitle, columnsTotal, columnsFinished, currentColumnTitle, cardsTotal, cardsFinished);
    }

    public SyncStatus withColumns(long total, long finished, String currentTitle) {
        return new SyncStatus(account, boardsTotal, boardsFinished, currentBoardTitle, total, finished, currentTitle, cardsTotal, cardsFinished);
    }

    public SyncStatus withCards(long total, long finished) {
        return new SyncStatus(account, boardsTotal, boardsFinished, currentBoardTitle, columnsTotal, columnsFinished, currentColumnTitle, total, finished);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Syncing ").append(account.accountName());
        if (boardsTotal > 0) {
            sb.append(": Board ").append(boardsFinished).append("/").append(boardsTotal);
            if (currentBoardTitle != null) sb.append(" (").append(currentBoardTitle).append(")");
        }
        if (columnsTotal > 0) {
            sb.append(", Column ").append(columnsFinished).append("/").append(columnsTotal);
        }
        if (cardsTotal > 0) {
            sb.append(", Card ").append(cardsFinished).append("/").append(cardsTotal);
        }
        return sb.toString();
    }
}
