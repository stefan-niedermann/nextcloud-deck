package it.niedermann.nextcloud.deck.cli.commands.board.subcommands;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "get",
        mixinStandardHelpOptions = true,
        description = "Get a board by its ID")
public class BoardGetCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(BoardGetCmd.class.getName());

    @Option(names = "--localId", description = "Local ID of the board")
    Long localId;

    @Option(names = "--remoteId", description = "Remote ID of the board")
    Long remoteId;

    @Inject
    BoardArgResolver boardArgResolver;

    @Inject
    GetBoardUseCase getBoardUseCase;

    @Override
    public Integer call() {
        try {
            if (localId != null) {
                final var board = Maybe.fromPublisher(getBoardUseCase.execute(new Board.ID(localId))).blockingGet();
                System.out.println(board);
                return 0;
            }

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

            final var board = Maybe.fromPublisher(getBoardUseCase.execute(parsedArgs.boardId())).blockingGet();
            System.out.println(board);

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
