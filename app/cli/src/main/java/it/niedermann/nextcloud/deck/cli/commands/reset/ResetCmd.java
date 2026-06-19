package it.niedermann.nextcloud.deck.cli.commands.reset;

import java.nio.file.Files;
import java.util.concurrent.Callable;

import it.niedermann.nextcloud.deck.app.shared.Util;
import it.niedermann.nextcloud.deck.domain.repository.StateRepository;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;

@Command(name = "reset",
        description = "Reset the local database and preferences")
public class ResetCmd implements Callable<Integer> {

    @Inject
    StateRepository stateRepository;

    @Override
    public Integer call() throws Exception {

        System.out.print("Clearing state…");
        stateRepository.reset().join();
        System.out.println(" ✓");

        System.out.print("Deleting local SQLite database…");
        final var pathDatabase = Util.getDatabasePath();
        Files.deleteIfExists(pathDatabase);
        System.out.println(" ✓");

        return 0;
    }
}
