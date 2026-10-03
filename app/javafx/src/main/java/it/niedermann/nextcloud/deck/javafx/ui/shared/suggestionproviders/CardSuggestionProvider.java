package it.niedermann.nextcloud.deck.javafx.ui.shared.suggestionproviders;

import com.dlsc.gemsfx.SearchField;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.cards.SearchCardsUseCase;
import jakarta.inject.Inject;
import javafx.util.Callback;

public class CardSuggestionProvider implements Callback<SearchField.SearchFieldSuggestionRequest, Collection<Card>> {

    private static final Logger logger = Logger.getLogger(CardSuggestionProvider.class.getName());

    private final SearchCardsUseCase searchCardsUseCase;
    private Board.ID boardId;
    private Card.ID excludeId;

    @Inject
    public CardSuggestionProvider(
            SearchCardsUseCase searchCardsUseCase
    ) {
        this.searchCardsUseCase = searchCardsUseCase;
    }

    public void setBoardId(Board.ID boardId) {
        this.boardId = boardId;
    }

    public void setExcludeId(Card.ID excludeId) {
        this.excludeId = excludeId;
    }

    @Override
    public Collection<Card> call(SearchField.SearchFieldSuggestionRequest param) {
        try {
            if (boardId == null) {
                return Maybe.fromPublisher(searchCardsUseCase.execute(param.getUserText(), excludeId))
                        .timeout(5, TimeUnit.SECONDS)
                        .blockingGet();
            }
            return Maybe.fromPublisher(searchCardsUseCase.execute(boardId, param.getUserText(), excludeId))
                    .timeout(5, TimeUnit.SECONDS)
                    .blockingGet();
        } catch (Exception e) {
            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof InterruptedException) {
                    return Collections.emptyList();
                }
                cause = cause.getCause();
            }
            logger.log(Level.WARNING, "Failed to fetch card suggestions", e);
            return Collections.emptyList();
        }
    }
}
