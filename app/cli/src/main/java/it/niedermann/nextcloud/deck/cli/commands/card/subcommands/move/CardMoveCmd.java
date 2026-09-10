package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.move;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.app.shared.args.column.ColumnArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.column.ColumnRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "move",
        mixinStandardHelpOptions = true,
        description = "Move a card to another column")
public class CardMoveCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(CardMoveCmd.class.getName());

    @Option(names = "--cardId", description = "Local ID of the card")
    Long cardId;

    @Option(names = "--cardRemoteId", description = "Remote ID of the card")
    Long cardRemoteId;

    @Option(names = "--columnId", description = "Local ID of the target column")
    Long columnId;

    @Option(names = "--columnRemoteId", description = "Remote ID of the target column")
    Long columnRemoteId;

    @Option(names = {"-o", "--order"}, description = "Order in the target column", defaultValue = "0")
    int order;

    @Inject
    CardArgResolver cardArgResolver;

    @Inject
    ColumnArgResolver columnArgResolver;

    @Inject
    MoveCardUseCase moveCardUseCase;

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

            final Column.ID finalColumnId;
            if (columnId != null) {
                finalColumnId = new Column.ID(columnId);
            } else if (columnRemoteId != null) {
                final var parsedArgs = Maybe.fromPublisher(columnArgResolver.resolve(new ColumnRawArgs.RemoteColumn(new Column.RemoteID(columnRemoteId)))).blockingGet();
                finalColumnId = parsedArgs.columnId();
            } else {
                System.err.println("Provide either --columnId or --columnRemoteId");
                return 2;
            }

            moveCardUseCase.execute(finalCardId, finalColumnId, order).join();
            System.out.println("Card moved.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
