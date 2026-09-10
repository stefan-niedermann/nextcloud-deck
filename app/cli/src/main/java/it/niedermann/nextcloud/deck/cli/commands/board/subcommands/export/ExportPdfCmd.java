package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.export;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.export.ExportBoardUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "pdf",
        mixinStandardHelpOptions = true,
        description = "Export board as PDF")
public class ExportPdfCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(ExportPdfCmd.class.getName());

    @Option(names = "--boardId", description = "Local ID of the board")
    Long boardId;

    @Option(names = "--boardRemoteId", description = "Remote ID of the board")
    Long boardRemoteId;

    @Option(names = {"-o", "--output"}, description = "Output file path", required = true)
    Path output;

    @Inject
    BoardArgResolver boardArgResolver;

    @Inject
    ExportBoardUseCase exportBoardUseCase;

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

            final var pdfBytes = Maybe.fromPublisher(exportBoardUseCase.toPdf(finalBoardId)).blockingGet();
            try (FileOutputStream fos = new FileOutputStream(output.toFile())) {
                fos.write(pdfBytes);
            }

            System.out.println("Board exported to " + output.toAbsolutePath());

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
