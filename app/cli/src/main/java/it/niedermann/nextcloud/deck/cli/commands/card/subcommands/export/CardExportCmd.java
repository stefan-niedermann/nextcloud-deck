package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.export;

import picocli.CommandLine.Command;

@Command(name = "export",
        mixinStandardHelpOptions = true,
        description = "Export card",
        subcommands = {
                CardExportOdtCmd.class,
                CardExportPdfCmd.class
        })
public class CardExportCmd {
}
