package it.niedermann.nextcloud.deck.cli;

import it.niedermann.nextcloud.deck.cli.commands.RootCmd;
import it.niedermann.nextcloud.deck.cli.di.AppComponent;
import it.niedermann.nextcloud.deck.cli.di.CommandFactory;
import picocli.CommandLine;

public class CliApplication {

    public CliApplication(AppComponent appComponent, String... args) {
        final var commandFactory = new CommandFactory(appComponent);
        final var rootCmd = new CommandLine(RootCmd.class, commandFactory);
        final int exitCode = rootCmd.execute(args);
        System.exit(exitCode);
    }
}