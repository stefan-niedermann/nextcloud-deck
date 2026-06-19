package it.niedermann.nextcloud.deck.cli.commands;

import it.niedermann.nextcloud.deck.cli.commands.account.AccountCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.BoardCmd;
import it.niedermann.nextcloud.deck.cli.commands.reset.ResetCmd;
import picocli.CommandLine.Command;

@Command(subcommands = {
        ResetCmd.class,
        AccountCmd.class,
        BoardCmd.class
})
public class RootCmd {
}
