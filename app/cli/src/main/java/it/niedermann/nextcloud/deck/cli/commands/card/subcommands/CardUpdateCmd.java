package it.niedermann.nextcloud.deck.cli.commands.card.subcommands;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import it.niedermann.nextcloud.deck.domain.usecases.cards.UpdateCardUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "update",
        mixinStandardHelpOptions = true,
        description = "Update a card")
public class CardUpdateCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(CardUpdateCmd.class.getName());

    @Option(names = "--localId", description = "Local ID of the card")
    Long localId;

    @Option(names = "--remoteId", description = "Remote ID of the card")
    Long remoteId;

    @Option(names = {"-t", "--title"}, description = "New title of the card")
    String title;

    @Option(names = {"-d", "--description"}, description = "New description of the card")
    String description;

    @Inject
    CardArgResolver cardArgResolver;

    @Inject
    CardRepository cardRepository;

    @Inject
    UpdateCardUseCase updateCardUseCase;

    @Override
    public Integer call() {
        try {
            final Card.ID cardId;
            if (localId != null) {
                cardId = new Card.ID(localId);
            } else if (remoteId != null) {
                cardId = Maybe.fromPublisher(cardArgResolver.resolve(new CardRawArgs.RemoteCard(new Card.RemoteID(remoteId)))).blockingGet();
            } else {
                System.err.println("Provide either --localId or --remoteId");
                return 2;
            }

            final var card = Maybe.fromPublisher(cardRepository.getCard(cardId)).blockingGet();

            final var updatedCard = new Card(
                    card.id(),
                    card.remoteId(),
                    card.columnId(),
                    card.createdAt(),
                    card.order(),
                    title != null ? title : card.title(),
                    description != null ? description : card.description(),
                    card.type(),
                    card.ownerId(),
                    card.labels(),
                    card.assignees(),
                    card.dependents(),
                    card.startDate(),
                    card.dueDate(),
                    card.done(),
                    card.color(),
                    card.archived(),
                    card.notified(),
                    card.overdue(),
                    card.commentsUnread(),
                    card.status(),
                    card.lastModified(),
                    card.etag()
            );

            updateCardUseCase.execute(updatedCard).join();
            System.out.println("Card updated.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
