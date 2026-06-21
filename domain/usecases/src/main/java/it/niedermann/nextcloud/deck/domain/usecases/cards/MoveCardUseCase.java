package it.niedermann.nextcloud.deck.domain.usecases.cards;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class MoveCardUseCase {

    private final CardRepository cardRepository;

    @Inject
    public MoveCardUseCase(
            CardRepository cardRepository
    ) {
        this.cardRepository = cardRepository;
    }

    public CompletableFuture<Void> execute(Card card, long targetColumnId, int targetOrder) {
        // FIXME: If targetColumnId is on another board or account, we need to mark the moved card as deleted in the origin, not only override the ID
        final var movedCard = new Card(
                card.id(),
                card.accountId(),
                card.boardId(),
                targetColumnId,
                card.createdAt(),
                card.deletedAt(),
                targetOrder,
                card.title(),
                card.description(),
                card.labels(),
                card.assignees(),
                card.attachments(),
                card.startDate(),
                card.dueDate(),
                card.done(),
                card.color(),
                card.dependents(),
                card.archived(),
                card.notified(),
                card.overdue(),
                card.commentsUnread()
        );

        return cardRepository.updateCard(movedCard);
    }
}
