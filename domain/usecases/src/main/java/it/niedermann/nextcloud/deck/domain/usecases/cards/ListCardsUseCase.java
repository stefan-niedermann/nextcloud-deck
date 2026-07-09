package it.niedermann.nextcloud.deck.domain.usecases.cards;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class ListCardsUseCase {

    private final CardRepository cardRepository;

    @Inject
    public ListCardsUseCase(
            CardRepository cardRepository
    ) {
        this.cardRepository = cardRepository;
    }

    public Flow.Publisher<List<Card>> execute(Column.ID columnId) {
        return cardRepository.getNotDeletedCards(columnId);
    }

    public Flow.Publisher<Map<Column, List<Card>>> executeForBoard(Board.ID boardId) {
        return cardRepository.getNotDeletedCardsByColumn(boardId);
    }
}
