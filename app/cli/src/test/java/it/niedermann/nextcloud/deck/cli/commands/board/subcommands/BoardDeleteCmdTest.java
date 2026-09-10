package it.niedermann.nextcloud.deck.cli.commands.board.subcommands;

import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.boards.DeleteBoardUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class BoardDeleteCmdTest {

    @Mock
    BoardArgResolver boardArgResolver;

    @Mock
    DeleteBoardUseCase deleteBoardUseCase;

    @InjectMocks
    BoardDeleteCmd boardDeleteCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCallLocalId() {
        final var boardId = new Board.ID(2L);
        boardDeleteCmd.localId = 2L;

        when(deleteBoardUseCase.execute(boardId)).thenReturn(CompletableFuture.completedFuture(null));

        final var result = boardDeleteCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
