package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.label;

import picocli.CommandLine.Command;

@Command(name = "label",
        mixinStandardHelpOptions = true,
        description = "Manage labels",
        subcommands = {
                LabelAddCmd.class,
                LabelDeleteCmd.class,
                LabelListCmd.class,
                LabelSearchCmd.class,
                LabelUpdateCmd.class
        })
public class BoardLabelCmd {
}
