package it.niedermann.nextcloud.deck.ui.stack;

public interface DeleteStackListener {
    void onDeleteStack(long accountId, long boardId, long stackId);
}