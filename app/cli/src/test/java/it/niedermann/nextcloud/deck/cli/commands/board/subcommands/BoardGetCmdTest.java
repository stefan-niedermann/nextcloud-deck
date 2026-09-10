package it.niedermann.nextcloud.deck.cli.commands.board.subcommands;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardParsedArgs;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BoardGetCmdTest {

    @Mock
    BoardArgResolver boardArgResolver;

    @Mock
    GetBoardUseCase getBoardUseCase;

    @InjectMocks
    BoardGetCmd boardGetCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCallLocalId() {
        final var boardId = new Board.ID(2L);
        final var board = new Board(
                boardId,
                "Test Board",
                new Color(0, 0, 0),
                new Board.Permissions(true, true, true, true)
        );
        boardGetCmd.localId = 2L;

        when(getBoardUseCase.execute(boardId)).thenReturn(Flowable.just(board));

        final var result = boardGetCmd.call();

        assertThat(result).isEqualTo(0);
    }

    @Test
    void testCallRemoteId() {
        final var accountId = new Account.ID(1L);
        final var boardId = new Board.ID(2L);
        final var board = new Board(
                boardId,
                "Test Board",
                new Color(0, 0, 0),
                new Board.Permissions(true, true, true, true)
        );
        boardGetCmd.remoteId = 123L;

        when(boardArgResolver.resolve(any(BoardRawArgs.RemoteBoard.class)))
                .thenReturn(Flowable.just(new BoardParsedArgs(accountId, boardId)));
        when(getBoardUseCase.execute(boardId)).thenReturn(Flowable.just(board));

        final var result = boardGetCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
