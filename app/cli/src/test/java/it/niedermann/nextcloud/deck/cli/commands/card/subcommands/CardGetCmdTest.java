package it.niedermann.nextcloud.deck.cli.commands.card.subcommands;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CardGetCmdTest {

    @Mock
    CardArgResolver cardArgResolver;

    @Mock
    CardRepository cardRepository;

    @InjectMocks
    CardGetCmd cardGetCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCallLocalId() {
        final var cardId = new Card.ID(3L);
        final var card = new Card(
                cardId,
                null,
                new Column.ID(2L),
                OffsetDateTime.now(),
                0,
                "Card",
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
        cardGetCmd.localId = 3L;

        when(cardRepository.getCard(cardId)).thenReturn(Flowable.just(card));

        final var result = cardGetCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
