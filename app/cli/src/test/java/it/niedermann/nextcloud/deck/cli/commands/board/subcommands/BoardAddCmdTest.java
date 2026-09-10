package it.niedermann.nextcloud.deck.cli.commands.board.subcommands;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.CreateBoard;
import it.niedermann.nextcloud.deck.domain.usecases.boards.AddBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BoardAddCmdTest {

    @Mock
    GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Mock
    AddBoardUseCase addBoardUseCase;

    @InjectMocks
    BoardAddCmd boardAddCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCall() {
        final var accountId = new Account.ID(1L);
        final var boardId = new Board.ID(2L);
        boardAddCmd.title = "New Board";

        when(getCurrentAccountUseCase.execute()).thenReturn(CompletableFuture.completedFuture(accountId));
        when(addBoardUseCase.addBoard(any(CreateBoard.class))).thenReturn(CompletableFuture.completedFuture(boardId));

        final var result = boardAddCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
