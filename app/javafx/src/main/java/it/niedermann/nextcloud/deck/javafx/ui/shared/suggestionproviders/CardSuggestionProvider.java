package it.niedermann.nextcloud.deck.javafx.ui.shared.suggestionproviders;

import com.dlsc.gemsfx.SearchField;

import java.util.Collection;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.cards.SearchCardsUseCase;
import jakarta.inject.Inject;
import javafx.util.Callback;

public class CardSuggestionProvider implements Callback<SearchField.SearchFieldSuggestionRequest, Collection<Card>> {

    private final SearchCardsUseCase searchCardsUseCase;
    private Board.ID boardId;

    @Inject
    public CardSuggestionProvider(
            SearchCardsUseCase searchCardsUseCase
    ) {
        this.searchCardsUseCase = searchCardsUseCase;
    }

    public void setBoardId(Board.ID boardId) {
        this.boardId = boardId;
    }

    @Override
    public Collection<Card> call(SearchField.SearchFieldSuggestionRequest param) {
        if (boardId == null) {
            return Maybe.fromPublisher(searchCardsUseCase.execute(param.getUserText())).blockingGet();
        }
        return Maybe.fromPublisher(searchCardsUseCase.execute(boardId, param.getUserText())).blockingGet();
    }
}
