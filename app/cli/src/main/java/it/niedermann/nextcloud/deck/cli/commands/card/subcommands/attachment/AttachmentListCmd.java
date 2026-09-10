package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.attachment;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.ListAttachmentsUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "list",
        mixinStandardHelpOptions = true,
        description = "List all attachments of a card")
public class AttachmentListCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(AttachmentListCmd.class.getName());

    @Option(names = "--cardId", description = "Local ID of the card")
    Long cardId;

    @Option(names = "--cardRemoteId", description = "Remote ID of the card")
    Long cardRemoteId;

    @Inject
    CardArgResolver cardArgResolver;

    @Inject
    ListAttachmentsUseCase listAttachmentsUseCase;

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

            final var attachments = Maybe.fromPublisher(listAttachmentsUseCase.execute(finalCardId)).blockingGet();

            for (final var attachment : attachments) {
                System.out.println(attachment.id().value() + ": " + attachment.filename() + " (" + attachment.size().bytes() + " bytes)");
            }

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
