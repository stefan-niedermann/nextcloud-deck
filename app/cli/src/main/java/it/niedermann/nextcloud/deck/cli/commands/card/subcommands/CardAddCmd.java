package it.niedermann.nextcloud.deck.cli.commands.card.subcommands;

import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateCard;
import it.niedermann.nextcloud.deck.domain.usecases.cards.AddCardUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "add",
        mixinStandardHelpOptions = true,
        description = "Add a new card to a column")
public class CardAddCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(CardAddCmd.class.getName());

    @Option(names = {"-t", "--title"}, description = "Title of the card", required = true)
    String title;

    @Option(names = "--columnId", description = "Local ID of the column", required = true)
    Long columnId;

    @Inject
    AddCardUseCase addCardUseCase;

    @Override
    public Integer call() {
        try {
            final var createCard = new CreateCard(new Column.ID(columnId), title);
            addCardUseCase.execute(createCard).join();

            System.out.println("Card created.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
