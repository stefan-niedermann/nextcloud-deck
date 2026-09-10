package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.label;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "search",
        mixinStandardHelpOptions = true,
        description = "Search for labels")
public class LabelSearchCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(LabelSearchCmd.class.getName());

    @Option(names = {"-q", "--query"}, description = "Search query", required = true)
    String query;

    @Inject
    LabelRepository labelRepository;

    @Override
    public Integer call() {
        try {
            final var labels = Maybe.fromPublisher(labelRepository.find(query)).blockingGet();

            for (final var label : labels) {
                System.out.println(label.id().value() + ": " + label.title());
            }

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
