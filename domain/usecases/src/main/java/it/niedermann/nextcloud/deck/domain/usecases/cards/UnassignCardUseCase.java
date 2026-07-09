package it.niedermann.nextcloud.deck.domain.usecases.cards;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import jakarta.inject.Inject;

public class UnassignCardUseCase {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    @Inject
    public UnassignCardUseCase(
            UserRepository userRepository,
            CardRepository cardRepository
    ) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    public CompletableFuture<Void> execute(Card.ID cardId, User.ID userId) {
        return cardRepository.unassignUser(cardId, userId);
    }
}
