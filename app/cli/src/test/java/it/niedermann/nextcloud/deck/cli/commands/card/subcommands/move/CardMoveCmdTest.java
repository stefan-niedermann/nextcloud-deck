package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.move;

import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.column.ColumnArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CardMoveCmdTest {

    @Mock
    CardArgResolver cardArgResolver;

    @Mock
    ColumnArgResolver columnArgResolver;

    @Mock
    MoveCardUseCase moveCardUseCase;

    @InjectMocks
    CardMoveCmd cardMoveCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCall() {
        final var cardId = new Card.ID(1L);
        final var columnId = new Column.ID(2L);
        cardMoveCmd.cardId = 1L;
        cardMoveCmd.columnId = 2L;
        cardMoveCmd.order = 3;

        when(moveCardUseCase.execute(cardId, columnId, 3)).thenReturn(CompletableFuture.completedFuture(null));

        final var result = cardMoveCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
