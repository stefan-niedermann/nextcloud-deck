package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.copy;

import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.column.ColumnArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.usecases.cards.CopyCardUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CardCopyCmdTest {

    @Mock
    CardArgResolver cardArgResolver;

    @Mock
    ColumnArgResolver columnArgResolver;

    @Mock
    CopyCardUseCase copyCardUseCase;

    @InjectMocks
    CardCopyCmd cardCopyCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCall() {
        final var cardId = new Card.ID(1L);
        final var columnId = new Column.ID(2L);
        cardCopyCmd.cardId = 1L;
        cardCopyCmd.columnId = 2L;
        cardCopyCmd.order = 3;

        when(copyCardUseCase.execute(cardId, columnId, 3)).thenReturn(CompletableFuture.completedFuture(null));

        final var result = cardCopyCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
