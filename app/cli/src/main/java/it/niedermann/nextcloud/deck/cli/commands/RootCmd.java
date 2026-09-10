package it.niedermann.nextcloud.deck.cli.commands;

import it.niedermann.nextcloud.deck.cli.commands.account.AccountCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.BoardCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.CardCmd;
import it.niedermann.nextcloud.deck.cli.commands.reset.ResetCmd;
import it.niedermann.nextcloud.deck.cli.commands.sync.SyncCmd;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "deck",
        mixinStandardHelpOptions = true,
        description = "Nextcloud Deck CLI client",
        subcommands = {
                CommandLine.HelpCommand.class,
                ResetCmd.class,
                AccountCmd.class,
                BoardCmd.class,
                CardCmd.class,
                SyncCmd.class
        })
public class RootCmd {
}
