package it.niedermann.nextcloud.deck.ui.stack;

public interface EditStackListener {
    void onCreateStack(String title);

    void onUpdateStack(long stackId, String title);
}