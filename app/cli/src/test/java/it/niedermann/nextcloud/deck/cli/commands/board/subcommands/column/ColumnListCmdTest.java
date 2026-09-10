package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.column;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ColumnListCmdTest {

    @Mock
    BoardArgResolver boardArgResolver;

    @Mock
    ListColumnsUseCase listColumnsUseCase;

    @InjectMocks
    ColumnListCmd columnListCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCall() {
        final var boardId = new Board.ID(1L);
        final var column = new Column(
                new Column.ID(2L),
                boardId,
                "Test Column",
                0,
                false,
                null,
                2L,
                null,
                null,
                DBStatus.UP_TO_DATE,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                null
        );
        columnListCmd.boardId = 1L;

        when(listColumnsUseCase.execute(boardId)).thenReturn(Flowable.just(List.of(column)));

        final var result = columnListCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
