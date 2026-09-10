package it.niedermann.nextcloud.deck.cli.commands.board.subcommands;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.UpdateBoardUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BoardUpdateCmdTest {

    @Mock
    BoardArgResolver boardArgResolver;

    @Mock
    GetBoardUseCase getBoardUseCase;

    @Mock
    UpdateBoardUseCase updateBoardUseCase;

    @InjectMocks
    BoardUpdateCmd boardUpdateCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCall() {
        final var boardId = new Board.ID(2L);
        final var board = new Board(
                boardId,
                "Old Title",
                new Color(0, 0, 0),
                new Board.Permissions(true, true, true, true)
        );
        boardUpdateCmd.localId = 2L;
        boardUpdateCmd.title = "New Title";

        when(getBoardUseCase.execute(boardId)).thenReturn(Flowable.just(board));
        when(updateBoardUseCase.execute(any(Board.class))).thenReturn(CompletableFuture.completedFuture(null));

        final var result = boardUpdateCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
