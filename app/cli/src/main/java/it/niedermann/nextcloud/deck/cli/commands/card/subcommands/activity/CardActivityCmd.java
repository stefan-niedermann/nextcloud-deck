package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.activity;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.activities.ListPreviewActivitiesUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "activity",
        mixinStandardHelpOptions = true,
        description = "List all activities of a card")
public class CardActivityCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(CardActivityCmd.class.getName());

    @Option(names = "--cardId", description = "Local ID of the card")
    Long cardId;

    @Option(names = "--cardRemoteId", description = "Remote ID of the card")
    Long cardRemoteId;

    @Inject
    CardArgResolver cardArgResolver;

    @Inject
    ListPreviewActivitiesUseCase listPreviewActivitiesUseCase;

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

            final var activities = Maybe.fromPublisher(listPreviewActivitiesUseCase.execute(finalCardId)).blockingGet();

            for (final var preview : activities) {
                final var activity = preview.activity();
                System.out.println(activity.id().value() + " [" + activity.createdAt() + "]: " + activity.subject());
            }

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
