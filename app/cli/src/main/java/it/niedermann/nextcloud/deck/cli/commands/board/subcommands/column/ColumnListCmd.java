package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.column;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnsUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "list",
        mixinStandardHelpOptions = true,
        description = "List all columns of a board")
public class ColumnListCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(ColumnListCmd.class.getName());

    @Option(names = "--boardId", description = "Local ID of the board")
    Long boardId;

    @Option(names = "--boardRemoteId", description = "Remote ID of the board")
    Long boardRemoteId;

    @Inject
    BoardArgResolver boardArgResolver;

    @Inject
    ListColumnsUseCase listColumnsUseCase;

    @Override
    public Integer call() {
        try {
            final Board.ID finalBoardId;
            if (boardId != null) {
                finalBoardId = new Board.ID(boardId);
            } else {
                final BoardRawArgs rawArgs;
                if (boardRemoteId != null) {
                    rawArgs = new BoardRawArgs.RemoteBoard(new Board.RemoteID(boardRemoteId));
                } else {
                    rawArgs = new BoardRawArgs.CurrentBoardOfCurrentAccount();
                }

                final var parsedArgs = Maybe.fromPublisher(boardArgResolver.resolve(rawArgs)).blockingGet();
                if (parsedArgs.boardId() == null) {
                    System.err.println("Board not found.");
                    return 2;
                }
                finalBoardId = parsedArgs.boardId();
            }

            final var columns = Maybe.fromPublisher(listColumnsUseCase.execute(finalBoardId)).blockingGet();

            for (final var column : columns) {
                System.out.println(column.id().value() + ": " + column.title() + " (Order: " + column.order() + ")");
            }

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
