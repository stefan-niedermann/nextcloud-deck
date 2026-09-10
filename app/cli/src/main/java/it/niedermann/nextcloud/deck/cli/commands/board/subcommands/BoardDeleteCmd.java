package it.niedermann.nextcloud.deck.cli.commands.board.subcommands;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.boards.DeleteBoardUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "delete",
        mixinStandardHelpOptions = true,
        description = "Delete a board")
public class BoardDeleteCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(BoardDeleteCmd.class.getName());

    @Option(names = "--localId", description = "Local ID of the board")
    Long localId;

    @Option(names = "--remoteId", description = "Remote ID of the board")
    Long remoteId;

    @Inject
    BoardArgResolver boardArgResolver;

    @Inject
    DeleteBoardUseCase deleteBoardUseCase;

    @Override
    public Integer call() {
        try {
            final Board.ID boardId;
            if (localId != null) {
                boardId = new Board.ID(localId);
            } else {
                final BoardRawArgs rawArgs;
                if (remoteId != null) {
                    rawArgs = new BoardRawArgs.RemoteBoard(new Board.RemoteID(remoteId));
                } else {
                    rawArgs = new BoardRawArgs.CurrentBoardOfCurrentAccount();
                }

                final var parsedArgs = Maybe.fromPublisher(boardArgResolver.resolve(rawArgs)).blockingGet();
                if (parsedArgs.boardId() == null) {
                    System.err.println("Board not found.");
                    return 2;
                }
                boardId = parsedArgs.boardId();
            }

            deleteBoardUseCase.execute(boardId).join();
            System.out.println("Board deleted.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
