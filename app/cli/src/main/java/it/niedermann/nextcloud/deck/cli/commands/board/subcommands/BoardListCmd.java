package it.niedermann.nextcloud.deck.cli.commands.board.subcommands;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "list",
        mixinStandardHelpOptions = true,
        description = "List all boards of the current account")
public class BoardListCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(BoardListCmd.class.getName());

    @Inject
    GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Inject
    ListBoardsUseCase listBoardsUseCase;

    @Override
    public Integer call() {
        try {
            final var accountId = getCurrentAccountUseCase.execute().join();
            final var boards = Maybe.fromPublisher(listBoardsUseCase.execute(accountId)).blockingGet();

            for (final var board : boards) {
                System.out.println(board.id().value() + ": " + board.title() + " (Remote: " + (board.remoteId() != null ? board.remoteId().value() : "none") + ")");
            }

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
