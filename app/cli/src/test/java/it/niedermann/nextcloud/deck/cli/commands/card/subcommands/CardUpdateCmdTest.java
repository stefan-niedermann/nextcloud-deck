package it.niedermann.nextcloud.deck.cli.commands.card.subcommands;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import it.niedermann.nextcloud.deck.domain.usecases.cards.UpdateCardUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CardUpdateCmdTest {

    @Mock
    CardArgResolver cardArgResolver;

    @Mock
    CardRepository cardRepository;

    @Mock
    UpdateCardUseCase updateCardUseCase;

    @InjectMocks
    CardUpdateCmd cardUpdateCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCall() {
        final var cardId = new Card.ID(3L);
        final var card = new Card(
                cardId,
                null,
                new Column.ID(2L),
                OffsetDateTime.now(),
                0,
                "Old Title",
                "",
                "plain",
                null,
                Set.of(),
                Set.of(),
                List.of(),
                null,
                null,
                null,
                new Color(0, 0, 0),
                false,
                false,
                0,
                0,
                DBStatus.UP_TO_DATE,
                OffsetDateTime.now(),
                null
        );
        cardUpdateCmd.localId = 3L;
        cardUpdateCmd.title = "New Title";

        when(cardRepository.getCard(cardId)).thenReturn(Flowable.just(card));
        when(updateCardUseCase.execute(any(Card.class))).thenReturn(CompletableFuture.completedFuture(null));

        final var result = cardUpdateCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
