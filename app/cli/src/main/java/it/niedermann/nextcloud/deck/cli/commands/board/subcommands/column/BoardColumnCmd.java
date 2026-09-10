package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.column;

import picocli.CommandLine.Command;

@Command(name = "column",
        mixinStandardHelpOptions = true,
        description = "Manage columns",
        subcommands = {
                ColumnAddCmd.class,
                ColumnDeleteCmd.class,
                ColumnGetCmd.class,
                ColumnListCmd.class,
                ColumnUpdateCmd.class
        })
public class BoardColumnCmd {
}
