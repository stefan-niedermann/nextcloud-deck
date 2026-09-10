package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.share;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.usecases.boards.UpdateBoardShareUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "update",
        mixinStandardHelpOptions = true,
        description = "Update a share on a board")
public class ShareUpdateCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(ShareUpdateCmd.class.getName());

    @Option(names = "--boardId", description = "Local ID of the board")
    Long boardId;

    @Option(names = "--boardRemoteId", description = "Remote ID of the board")
    Long boardRemoteId;

    @Option(names = "--userId", description = "Remote ID of the user", required = true)
    String userId;

    @Option(names = "--read", description = "Permission: Read")
    Boolean read;

    @Option(names = "--edit", description = "Permission: Edit")
    Boolean edit;

    @Option(names = "--manage", description = "Permission: Manage")
    Boolean manage;

    @Option(names = "--share", description = "Permission: Share")
    Boolean share;

    @Inject
    BoardArgResolver boardArgResolver;

    @Inject
    UpdateBoardShareUseCase updateBoardShareUseCase;

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

            // NOTE: We don't have a way to fetch the current share permissions easily without listing all shares.
            // For simplicity, we assume default values if not provided.
            final var permissions = new Board.Permissions(
                    read != null ? read : true,
                    edit != null ? edit : false,
                    manage != null ? manage : false,
                    share != null ? share : false
            );
            updateBoardShareUseCase.execute(finalBoardId, new User.ID(userId), permissions).join();

            System.out.println("Share updated.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
