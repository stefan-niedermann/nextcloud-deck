package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.column;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.CreateColumn;
import it.niedermann.nextcloud.deck.domain.usecases.columns.AddColumnUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "add",
        mixinStandardHelpOptions = true,
        description = "Add a new column to a board")
public class ColumnAddCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(ColumnAddCmd.class.getName());

    @Option(names = {"-t", "--title"}, description = "Title of the column", required = true)
    String title;

    @Option(names = {"-o", "--order"}, description = "Order of the column", defaultValue = "0")
    int order;

    @Option(names = "--boardId", description = "Local ID of the board")
    Long boardId;

    @Option(names = "--boardRemoteId", description = "Remote ID of the board")
    Long boardRemoteId;

    @Inject
    BoardArgResolver boardArgResolver;

    @Inject
    AddColumnUseCase addColumnUseCase;

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

            final var createColumn = new CreateColumn(finalBoardId, title, order);
            addColumnUseCase.execute(createColumn).join();

            System.out.println("Column created.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
