package it.niedermann.nextcloud.deck.ui.main.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;

public class SearchResults {

    @Nullable
    public final Account account;

    @Nullable
    public final FullBoard fullBoard;

    @NonNull
    public final Map<Stack, List<FullCard>> result;

    @NonNull
    public final String term;

    public SearchResults() {
        this(null, null, Collections.emptyMap(), "");
    }

    public SearchResults(@Nullable Account account, @Nullable FullBoard fullBoard, @NonNull Map<Stack, List<FullCard>> result, @NonNull String term) {
        this.account = account;
        this.fullBoard = fullBoard;
        this.result = result;
        this.term = term;
    }
}