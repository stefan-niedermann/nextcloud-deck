package it.niedermann.nextcloud.deck.cli.commands.board.subcommands;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class BoardListCmdTest {

    @Mock
    GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Mock
    ListBoardsUseCase listBoardsUseCase;

    @InjectMocks
    BoardListCmd boardListCmd;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCall() {
        final var accountId = new Account.ID(1L);
        final var board = new Board(
                new Board.ID(2L),
                "Test Board",
                new Color(0, 0, 0),
                new Board.Permissions(true, true, true, true)
        );

        when(getCurrentAccountUseCase.execute()).thenReturn(CompletableFuture.completedFuture(accountId));
        when(listBoardsUseCase.execute(accountId)).thenReturn(Flowable.just(List.of(board)));

        final var result = boardListCmd.call();

        assertThat(result).isEqualTo(0);
    }
}
