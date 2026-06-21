package it.niedermann.nextcloud.deck.domain.usecases.cards;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class AddCardUseCase {

    private final CardRepository cardRepository;

    @Inject
    public AddCardUseCase(
            CardRepository cardRepository
    ) {
        this.cardRepository = cardRepository;
    }

    public CompletableFuture<Void> execute(long columnId, String title) {
        return cardRepository.createCard(new Card(-1,
                -1,
                -1,
                columnId,
                LocalDateTime.now(),
                null,
                0,
                title,
                "",
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptyList(),
                null,
                null,
                null,
                null,
                Collections.emptySet(),
                false,
                false,
                0,
                0));
    }
}
