package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.attachment;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.AddAttachmentUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "add",
        mixinStandardHelpOptions = true,
        description = "Add a new attachment to a card")
public class AttachmentAddCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(AttachmentAddCmd.class.getName());

    @Option(names = {"-f", "--file"}, description = "File path to attach", required = true)
    Path file;

    @Option(names = "--cardId", description = "Local ID of the card")
    Long cardId;

    @Option(names = "--cardRemoteId", description = "Remote ID of the card")
    Long cardRemoteId;

    @Inject
    CardArgResolver cardArgResolver;

    @Inject
    AddAttachmentUseCase addAttachmentUseCase;

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

            addAttachmentUseCase.execute(finalCardId, file).join();

            System.out.println("Attachment added.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
