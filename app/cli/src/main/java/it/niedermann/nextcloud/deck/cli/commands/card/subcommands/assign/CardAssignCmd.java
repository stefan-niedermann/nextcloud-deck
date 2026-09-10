package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.assign;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.usecases.cards.AssignCardUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "assign",
        mixinStandardHelpOptions = true,
        description = "Assign a user to a card")
public class CardAssignCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(CardAssignCmd.class.getName());

    @Option(names = "--cardId", description = "Local ID of the card")
    Long cardId;

    @Option(names = "--cardRemoteId", description = "Remote ID of the card")
    Long cardRemoteId;

    @Option(names = "--userId", description = "Remote ID of the user", required = true)
    String userId;

    @Inject
    CardArgResolver cardArgResolver;

    @Inject
    AssignCardUseCase assignCardUseCase;

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

            assignCardUseCase.execute(finalCardId, new User.ID(userId)).join();
            System.out.println("User assigned.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
