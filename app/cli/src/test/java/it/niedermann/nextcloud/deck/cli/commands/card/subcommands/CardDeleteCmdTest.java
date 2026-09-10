package it.niedermann.nextcloud.deck.cli.commands.card.subcommands;

import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.cards.DeleteCardUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CardDeleteCmdTest {

    @Mock
    CardArgResolver cardArgResolver;

    @Mock
    DeleteCardUseCase deleteCardUseCase;

    @InjectMocks
    CardDeleteCmd cardDeleteCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCallLocalId() {
        final var cardId = new Card.ID(3L);
        cardDeleteCmd.localId = 3L;

        when(deleteCardUseCase.execute(cardId)).thenReturn(CompletableFuture.completedFuture(null));

        final var result = cardDeleteCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
