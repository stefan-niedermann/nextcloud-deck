package it.niedermann.nextcloud.deck.cli.commands.card.subcommands;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Board;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class CardListCmdTest {

    @Mock
    BoardArgResolver boardArgResolver;

    @Mock
    CardRepository cardRepository;

    @InjectMocks
    CardListCmd cardListCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCall() {
        final var boardId = new Board.ID(1L);
        final var column = new Column(new Column.ID(2L), boardId, "Column", 0);
        final var card = new Card(
                new Card.ID(3L),
                null,
                column.id(),
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
        cardListCmd.boardId = 1L;

        when(cardRepository.getNotDeletedCardsByColumn(boardId)).thenReturn(Flowable.just(Map.of(column, List.of(card))));

        final var result = cardListCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
