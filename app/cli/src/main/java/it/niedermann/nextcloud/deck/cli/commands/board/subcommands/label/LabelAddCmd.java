package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.label;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.model.CreateLabel;
import it.niedermann.nextcloud.deck.domain.usecases.labels.AddLabelUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "add",
        mixinStandardHelpOptions = true,
        description = "Add a new label to a board")
public class LabelAddCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(LabelAddCmd.class.getName());

    @Option(names = {"-t", "--title"}, description = "Title of the label", required = true)
    String title;

    @Option(names = {"-c", "--color"}, description = "Color of the label (hex)", defaultValue = "000000")
    String colorHex;

    @Option(names = "--boardId", description = "Local ID of the board")
    Long boardId;

    @Option(names = "--boardRemoteId", description = "Remote ID of the board")
    Long boardRemoteId;

    @Inject
    BoardArgResolver boardArgResolver;

    @Inject
    AddLabelUseCase addLabelUseCase;

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

            final int r = Integer.valueOf(colorHex.substring(0, 2), 16);
            final int g = Integer.valueOf(colorHex.substring(2, 4), 16);
            final int b = Integer.valueOf(colorHex.substring(4, 6), 16);

            final var createLabel = new CreateLabel(finalBoardId, title, new Color(r, g, b));
            addLabelUseCase.execute(createLabel).join();

            System.out.println("Label created.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
