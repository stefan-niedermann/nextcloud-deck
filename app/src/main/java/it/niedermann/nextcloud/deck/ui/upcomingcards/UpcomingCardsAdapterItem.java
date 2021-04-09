package it.niedermann.nextcloud.deck.ui.upcomingcards;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;

public class UpcomingCardsAdapterItem {
    private final FullCard fullCard;
    private final Account account;
    private final long currentBoardLocalId;
    private final Long currentBoardRemoteId;
    private final boolean currentBoardHasEditPermission;

    public UpcomingCardsAdapterItem(FullCard fullCard, Account account, long currentBoardLocalId, Long currentBoardRemoteId, boolean currentBoardHasEditPermission) {
        this.fullCard = fullCard;
        this.account = account;
        this.currentBoardLocalId = currentBoardLocalId;
        this.currentBoardRemoteId = currentBoardRemoteId;
        this.currentBoardHasEditPermission = currentBoardHasEditPermission;
    }

    public FullCard getFullCard() {
        return fullCard;
    }

    public Account getAccount() {
        return account;
    }

    public Long getCurrentBoardLocalId() {
        return currentBoardLocalId;
    }

    public Long getCurrentBoardRemoteId() {
        return currentBoardRemoteId;
    }

    public boolean currentBoardHasEditPermission() {
        return currentBoardHasEditPermission;
    }
}