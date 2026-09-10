package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.export;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.export.ExportCardUseCase;
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
        description = "Export card as PDF")
public class CardExportPdfCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(CardExportPdfCmd.class.getName());

    @Option(names = "--cardId", description = "Local ID of the card")
    Long cardId;

    @Option(names = "--cardRemoteId", description = "Remote ID of the card")
    Long cardRemoteId;

    @Option(names = {"-o", "--output"}, description = "Output file path", required = true)
    Path output;

    @Inject
    CardArgResolver cardArgResolver;

    @Inject
    ExportCardUseCase exportCardUseCase;

    @Override
    public Integer call() {
        try {
            final Card.ID finalCardId;
            if (cardId != null) {
                finalCardId = new Card.ID(cardId);
            } else if (cardRemoteId != null) {
                finalCardId = Maybe.fromPublisher(cardArgResolver.resolve(new CardRawArgs.RemoteCard(new Card.RemoteID(cardRemoteId)))).blockingGet();
            } else {
                System.err.println("Provide either --cardId or --cardRemoteId");
                return 2;
            }

            final var pdfBytes = Maybe.fromPublisher(exportCardUseCase.toPdf(finalCardId)).blockingGet();
            try (FileOutputStream fos = new FileOutputStream(output.toFile())) {
                fos.write(pdfBytes);
            }

            System.out.println("Card exported to " + output.toAbsolutePath());

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
