package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.search;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "search",
        mixinStandardHelpOptions = true,
        description = "Search for cards")
public class CardSearchCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(CardSearchCmd.class.getName());

    @Option(names = {"-q", "--query"}, description = "Search query", required = true)
    String query;

    @Inject
    CardRepository cardRepository;

    @Override
    public Integer call() {
        try {
            final var cards = Maybe.fromPublisher(cardRepository.find(query)).blockingGet();

            for (final var card : cards) {
                System.out.println(card.id().value() + ": " + card.title());
            }

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
