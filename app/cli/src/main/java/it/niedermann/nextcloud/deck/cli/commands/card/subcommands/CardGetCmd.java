package it.niedermann.nextcloud.deck.cli.commands.card.subcommands;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "get",
        mixinStandardHelpOptions = true,
        description = "Get a card by its ID")
public class CardGetCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(CardGetCmd.class.getName());

    @Option(names = "--localId", description = "Local ID of the card")
    Long localId;

    @Option(names = "--remoteId", description = "Remote ID of the card")
    Long remoteId;

    @Inject
    CardArgResolver cardArgResolver;

    @Inject
    CardRepository cardRepository;

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
            System.out.println(card);

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
