package it.niedermann.nextcloud.deck.ui.upcomingcards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;

public class UpcomingCardsAdapterItem {
    @NonNull
    private final FullCard fullCard;
    @NonNull
    private final Account account;
    private final long currentBoardLocalId;
    @Nullable
    private final Long currentBoardRemoteId;
    private final boolean currentBoardHasEditPermission;

    public UpcomingCardsAdapterItem(@NonNull FullCard fullCard, @NonNull Account account, long currentBoardLocalId, @Nullable Long currentBoardRemoteId, boolean currentBoardHasEditPermission) {
        this.fullCard = fullCard;
        this.account = account;
        this.currentBoardLocalId = currentBoardLocalId;
        this.currentBoardRemoteId = currentBoardRemoteId;
        this.currentBoardHasEditPermission = currentBoardHasEditPermission;
    }

    public @NotNull FullCard getFullCard() {
        return fullCard;
    }

    public @NotNull Account getAccount() {
        return account;
    }

    public Long getCurrentBoardLocalId() {
        return currentBoardLocalId;
    }

    public @org.jetbrains.annotations.Nullable Long getCurrentBoardRemoteId() {
        return currentBoardRemoteId;
    }

    public boolean currentBoardHasEditPermission() {
        return currentBoardHasEditPermission;
    }
}