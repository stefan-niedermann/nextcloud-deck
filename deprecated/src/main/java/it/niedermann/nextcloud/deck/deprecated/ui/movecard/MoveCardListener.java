package it.niedermann.nextcloud.deck.deprecated.ui.movecard;

public interface MoveCardListener {
    void move(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId);
}
