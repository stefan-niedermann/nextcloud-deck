package it.niedermann.nextcloud.deck.ui.pickstack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;

public interface PickStackListener {
    void onStackPicked(@NonNull Account account, @Nullable Board board, @Nullable Stack stack);
}
