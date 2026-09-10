package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.share;

import picocli.CommandLine.Command;

@Command(name = "share",
        mixinStandardHelpOptions = true,
        description = "Manage shares",
        subcommands = {
                ShareAddCmd.class,
                ShareListCmd.class,
                ShareRemoveCmd.class,
                ShareUpdateCmd.class
        })
public class BoardShareCmd {
}
