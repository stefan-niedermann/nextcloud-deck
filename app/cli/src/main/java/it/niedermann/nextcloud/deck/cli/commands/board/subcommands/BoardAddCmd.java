package it.niedermann.nextcloud.deck.cli.commands.board.subcommands;

import it.niedermann.nextcloud.deck.domain.model.CreateBoard;
import it.niedermann.nextcloud.deck.domain.usecases.boards.AddBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "add",
        mixinStandardHelpOptions = true,
        description = "Add a new board to the current account")
public class BoardAddCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(BoardAddCmd.class.getName());

    @Option(names = {"-t", "--title"}, description = "Title of the board", required = true)
    String title;

    @Inject
    GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Inject
    AddBoardUseCase addBoardUseCase;

    @Override
    public Integer call() {
        try {
            final var accountId = getCurrentAccountUseCase.execute().join();
            final var createBoard = new CreateBoard(accountId, title);
            final var boardId = addBoardUseCase.addBoard(createBoard).join();

            System.out.println("Board created with local ID: " + boardId.value());

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
