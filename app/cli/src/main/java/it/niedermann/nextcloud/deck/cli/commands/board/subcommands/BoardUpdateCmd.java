package it.niedermann.nextcloud.deck.cli.commands.board.subcommands;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.UpdateBoardUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "update",
        mixinStandardHelpOptions = true,
        description = "Update a board")
public class BoardUpdateCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(BoardUpdateCmd.class.getName());

    @Option(names = "--localId", description = "Local ID of the board")
    Long localId;

    @Option(names = "--remoteId", description = "Remote ID of the board")
    Long remoteId;

    @Option(names = {"-t", "--title"}, description = "New title of the board")
    String title;

    @Inject
    BoardArgResolver boardArgResolver;

    @Inject
    GetBoardUseCase getBoardUseCase;

    @Inject
    UpdateBoardUseCase updateBoardUseCase;

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

            final var board = Maybe.fromPublisher(getBoardUseCase.execute(boardId)).blockingGet();

            final var updatedBoard = new Board(
                    board.id(),
                    title != null ? title : board.title(),
                    board.color(),
                    board.isOwner(),
                    board.archived(),
                    board.permissions(),
                    board.accountId(),
                    board.remoteId(),
                    board.status(),
                    board.lastModified(),
                    board.etag()
            );

            updateBoardUseCase.execute(updatedBoard).join();
            System.out.println("Board updated.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
