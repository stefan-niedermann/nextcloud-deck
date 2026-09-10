package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.share;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.usecases.boards.AddBoardShareUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "add",
        mixinStandardHelpOptions = true,
        description = "Add a new share to a board")
public class ShareAddCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(ShareAddCmd.class.getName());

    @Option(names = "--boardId", description = "Local ID of the board")
    Long boardId;

    @Option(names = "--boardRemoteId", description = "Remote ID of the board")
    Long boardRemoteId;

    @Option(names = "--userId", description = "Remote ID of the user", required = true)
    String userId;

    @Option(names = "--read", description = "Permission: Read", defaultValue = "true")
    boolean read;

    @Option(names = "--edit", description = "Permission: Edit", defaultValue = "false")
    boolean edit;

    @Option(names = "--manage", description = "Permission: Manage", defaultValue = "false")
    boolean manage;

    @Option(names = "--share", description = "Permission: Share", defaultValue = "false")
    boolean share;

    @Inject
    BoardArgResolver boardArgResolver;

    @Inject
    AddBoardShareUseCase addBoardShareUseCase;

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

            final var permissions = new Board.Permissions(read, edit, manage, share);
            addBoardShareUseCase.execute(finalBoardId, new User.ID(userId), permissions).join();

            System.out.println("Share added.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
